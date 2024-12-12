package edu.stanford.irt.eresources.marc;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.DateParser;
import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceConstants;
import edu.stanford.irt.eresources.LanguageMap;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.VersionComparator;
import edu.stanford.irt.eresources.marc.type.TypeFactory;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;
import edu.stanford.lane.catalog.TextHelper;

/**
 * An Eresource that encapsulates the marc Records from which it is derived.
 */
public abstract class AbstractMarcEresource extends MARCRecordSupport implements Eresource {

    private static final String BR = "<br/>";

    private static final int SORT_TITLE_MAX_LENGTH = 48;

    // patterns from SUL's indexer
    // https://github.com/sul-dlss/searchworks_traject_indexer/blob/28c9056dd318f8d17f2ec11e622d50981cdfcab0/lib/traject/common/marc_utils.rb#L391
    private static final Pattern[] TOC_LINEBREAK_PATTERNS = { Pattern.compile("[^\\S]--[^\\S]"),
            Pattern.compile(" {5}+"), Pattern.compile("--[^\\S]"), Pattern.compile("[^\\S]\\.-[^\\S]"),
            Pattern.compile("(?=(?:Chapter|Section|Appendix|Part|v\\.) \\d+[:\\.-]?\\s+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?=(?:Appendix|Section|Chapter) [XVI]+[\\.-]?)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?=[^\\d]\\d+[:\\.-]\\s+)"), Pattern.compile("(?=\\s{2,}\\d+\\s+)") };

    protected static final Pattern COLON_OR_SEMICOLON = Pattern.compile("[:;]");

    protected static final Pattern COMMA_DOLLAR = Pattern.compile(",$");

    protected static final Comparator<Version> COMPARATOR = new VersionComparator();

    protected static final int F008_07 = 7;

    protected static final int F008_11 = 11;

    protected static final int F008_15 = 15;

    protected static final int F008_35 = 35;

    protected static final int F008_38 = 38;

    protected static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss")
            .toFormatter();

    protected static final LanguageMap LANGUAGE_MAP = new LanguageMap();

    protected static final int LEADER_BYTE_06 = 6;

    protected static final int LEADER_BYTE_07 = 7;

    protected static final Pattern NAME_INITIAL_PERIOD = Pattern.compile(".* \\w\\.");

    protected static final String SEMICOLON_SPACE = "; ";

    protected static final Pattern SPACE_SLASH = Pattern.compile(" /");

    private static String maybeStripFinialPeriodFromAuthor(final String author) {
        return author.endsWith(".") && !NAME_INITIAL_PERIOD.matcher(author).matches()
                ? author.substring(0, author.length() - 1)
                : author;
    }

    protected List<Record> holdings;

    protected KeywordsStrategy keywordsStrategy;

    protected HTTPLaneLocationsService locationsService;

    protected Record marcRecord;

    protected String primaryType;

    protected Collection<String> types;

    protected List<Version> versions;

    @Override
    public Collection<String> getAbbreviatedTitles() {
        return getSubfieldData(getFields(this.marcRecord, "246").filter((final Field f) -> {
            Subfield i = f.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'i').findFirst()
                    .orElse(null);
            Subfield a = f.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'a').findFirst()
                    .orElse(null);
            return i != null && a != null && "Acronym/initialism:".equalsIgnoreCase(i.getData());
        }), "a").collect(Collectors.toSet());
    }

    @Override
    public Collection<String> getAlternativeTitles() {
        return getSubfieldData(this.marcRecord, "130|210|246|247", "a").collect(Collectors.toSet());
    }

    @Override
    public Collection<String> getBroadMeshTerms() {
        return getSubfieldData(getFields(this.marcRecord, "650")
                .filter((final Field f) -> f.getIndicator1() == '4' && "23".indexOf(f.getIndicator2()) > -1), "a")
                        .map(TextParserHelper::maybeStripTrailingPeriod).collect(Collectors.toSet());
    }

    @Override
    public String getDate() {
        String date = null;
        List<Field> fields773 = getFields(this.marcRecord, "773").toList();
        int subfieldWCount = 0;
        for (Field element : fields773) {
            List<Subfield> subfields = element.getSubfields();
            if (subfieldWCount == 0) {
                date = subfields.stream().filter((final Subfield s) -> s.getCode() == 'd').map(Subfield::getData)
                        .reduce((final String a, final String b) -> b).orElse(date);
            }
            if (subfields.stream().anyMatch((final Subfield s) -> s.getCode() == 'w')) {
                subfieldWCount += 1;
            }
        }
        if (date != null) {
            date = DateParser.parseDate(COLON_OR_SEMICOLON.matcher(date).replaceAll(" "));
        }
        if (null == date || "0".equals(date) || date.isEmpty()) {
            String field008 = getFields(this.marcRecord, "008").map(Field::getData).findFirst()
                    .orElse(EresourceConstants.EMPTY_008);
            String endDate = TextParserHelper.parseYear(field008.substring(F008_11, F008_15));
            String beginDate = TextParserHelper.parseYear(field008.substring(F008_07, F008_11));
            int year = 0;
            if (null != endDate) {
                year = Integer.parseInt(endDate);
            } else if (null != beginDate) {
                year = Integer.parseInt(beginDate);
            } else {
                year = getYear();
            }
            date = DateParser.parseDate(Integer.toString(year));
        }
        return date;
    }

    @Override
    public String getDescription() {
        // prefer 905s over 505s and 920s over 520s
        // 905/505 get linebreak parsing to improve formatting
        String tag = getFields(this.marcRecord, "920").count() > 0 ? "920" : "520";
        final String labelSummary = getFields(this.marcRecord, "520|920").count() > 0 ? "::Summary## " : "";
        StringBuilder sb = new StringBuilder(labelSummary);
        getSubfieldData(this.marcRecord, tag).forEach((final String s) -> {
            if (sb.length() > labelSummary.length()) {
                sb.append(' ');
            }
            sb.append(s);
        });
        tag = getFields(this.marcRecord, "905").count() > 0 ? "905" : "505";
        final String labelContents = getFields(this.marcRecord, "505|905").count() > 0 ? "::Contents##" + BR : "";
        // add breaks before the contents label if a summary is already present
        if (sb.length() > labelSummary.length() && !labelContents.isEmpty()) {
            sb.append(BR);
        }
        StringBuilder sbContents = new StringBuilder(labelContents);
        getSubfieldData(this.marcRecord, tag).forEach((final String s) -> {
            if (sbContents.length() > labelContents.length()) {
                sbContents.append(' ');
            }
            sbContents.append(s);
        });
        sb.append(replaceTOCLinebreaks(sbContents.toString()));
        return sb.length() > 0 ? sb.toString() : null;
    }

    @Override
    public String getId() {
        return getRecordType() + "-" + getRecordId();
    }

    @Override
    public Collection<String> getIsbns() {
        return MARCRecordSupport.getSubfieldData(this.marcRecord, "020", "az").map(String::trim)
                .map(TextHelper::cleanIsxn).toList();
    }

    @Override
    public Collection<String> getIssns() {
        return MARCRecordSupport.getSubfieldData(this.marcRecord, "022", "azlm").map(String::trim)
                .map(TextHelper::cleanIsxn).toList();
    }

    @Override
    public int[] getItemCount() {
        int[] itemCount = new int[3];
        String total = MARCRecordSupport.getSubfieldData(this.marcRecord, "888", "t").findFirst().orElse("0");
        String available = MARCRecordSupport.getSubfieldData(this.marcRecord, "888", "a").findFirst().orElse("0");
        String out = MARCRecordSupport.getSubfieldData(this.marcRecord, "888", "c").findFirst().orElse("0");
        itemCount[0] = Integer.parseInt(total);
        itemCount[1] = Integer.parseInt(available);
        itemCount[2] = Integer.parseInt(out);
        return itemCount;
    }

    @Override
    public String getKeywords() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.keywordsStrategy.getKeywords(this.marcRecord));
        this.holdings.stream().forEach((final Record holding) -> sb.append(this.keywordsStrategy.getKeywords(holding)));
        return sb.toString();
    }

    @Override
    public Collection<String> getMeshTerms() {
        return getSubfieldData(getFields(this.marcRecord, "650|651")
                .filter((final Field f) -> ("650".equals(f.getTag()) && "2356".indexOf(f.getIndicator2()) > -1)
                        || ("651".equals(f.getTag()) && f.getIndicator2() == '7')),
                "a").map(TextParserHelper::maybeStripTrailingPeriod).collect(Collectors.toSet());
    }

    @Override
    public String getPrimaryType() {
        if (this.primaryType == null) {
            this.primaryType = TypeFactory.getPrimaryType(this.marcRecord);
        }
        return this.primaryType;
    }

    @Override
    public Collection<String> getPublicationAuthors() {
        return Collections
                .unmodifiableCollection(
                        getSubfieldData(
                                getFields(this.marcRecord, "100|700").filter((final Field f) -> "100".equals(f.getTag())
                                        || ("700".equals(f.getTag()) && !(getPrimaryType().startsWith("Journal")))),
                                "a").map((final String s) -> COMMA_DOLLAR.matcher(s).replaceFirst(""))
                                        .map(AbstractMarcEresource::maybeStripFinialPeriodFromAuthor).toList());
    }

    @Override
    public String getPublicationAuthorsText() {
        String authorsText = getSubfieldData(this.marcRecord, "245", "c")
                // get the last c
                .reduce((final String a, final String b) -> b).orElse(null);
        if (authorsText == null) {
            StringBuilder sb = new StringBuilder();
            for (String auth : getPublicationAuthors()) {
                sb.append(auth).append(SEMICOLON_SPACE);
            }
            if (sb.toString().endsWith(SEMICOLON_SPACE)) {
                sb.delete(sb.length() - SEMICOLON_SPACE.length(), sb.length());
            }
            if (sb.length() > 0 && !sb.toString().endsWith(".")) {
                sb.append('.');
            }
            authorsText = sb.toString();
        }
        return authorsText;
    }

    @Override
    public String getPublicationDate() {
        return null;
    }

    @Override
    public String getPublicationIssue() {
        return null;
    }

    @Override
    public Collection<String> getPublicationLanguages() {
        Set<String> languages = new HashSet<>();
        String field008 = getFields(this.marcRecord, "008").map(Field::getData).findFirst()
                .orElse(EresourceConstants.EMPTY_008);
        String lang = "";
        if (field008.length() > F008_38) {
            lang = field008.substring(F008_35, F008_38).toLowerCase(Locale.US);
        }
        languages.add(LANGUAGE_MAP.getLanguage(lang.toLowerCase(Locale.US)));
        languages.addAll(getSubfieldData(this.marcRecord, "041").map(String::toLowerCase).map(LANGUAGE_MAP::getLanguage)
                .collect(Collectors.toSet()));
        return languages;
    }

    @Override
    public String getPublicationPages() {
        return null;
    }

    @Override
    public String getPublicationText() {
        StringBuilder sb = new StringBuilder();
        List<Field> fields773 = getFields(this.marcRecord, "773").toList();
        int subfieldWCount = 0;
        for (Field field733 : fields773) {
            if (subfieldWCount == 0 && sb.length() == 0) {
                sb.append(field733.getSubfields().stream().filter((final Subfield s) -> "tp".indexOf(s.getCode()) > -1)
                        .map(Subfield::getData).reduce((final String a, final String b) -> b).orElse(""));
                if (sb.length() > 0) {
                    sb.append(". ");
                }
            }
            for (Subfield subfield : field733.getSubfields()) {
                if ("dg".indexOf(subfield.getCode()) > -1) {
                    sb.append(' ').append(subfield.getData());
                } else if (subfieldWCount > 0 && subfield.getCode() == 't') {
                    sb.append("; ").append(subfield.getData());
                }
            }
            subfieldWCount = subfieldWCount
                    + (int) field733.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'w').count();
        }
        return sb.toString();
    }

    @Override
    public String getPublicationTitle() {
        List<Field> fields773 = getFields(this.marcRecord, "773").toList();
        int countOf733W = 0;
        String data = null;
        for (int i = 0; i < fields773.size() && countOf733W == 0; i++) {
            for (Subfield subfield : fields773.get(i).getSubfields()) {
                char code = subfield.getCode();
                if (code == 't' || code == 'p') {
                    data = subfield.getData();
                } else if (code == 'w') {
                    countOf733W++;
                }
            }
        }
        return data;
    }

    @Override
    public Collection<String> getPublicationTypes() {
        return Collections.emptySet();
    }

    @Override
    public String getPublicationVolume() {
        return null;
    }

    @Override
    public String getRecordId() {
        return MARCRecordSupport.getRecordId(this.marcRecord);
    }

    @Override
    public String getRecordType() {
        return "bib";
    }

    @Override
    public String getShortTitle() {
        return getSubfieldData(this.marcRecord, "245", "a").findFirst().orElse(null);
    }

    @Override
    public String getSortTitle() {
        StringBuilder sb = getTitleStringBuilder(getFields(this.marcRecord, "245").findFirst().orElse(null));
        int offset = getFields(this.marcRecord, "245")
                .map((final Field f) -> Integer.valueOf(f.getIndicator2()) - SORT_TITLE_MAX_LENGTH).findFirst()
                .orElse(0);
        if (offset < 0 || offset > sb.length()) {
            offset = 0;
        }
        return sb.substring(offset);
    }

    @Override
    public String getTitle() {
        StringBuilder sb = getTitleStringBuilder(getFields(this.marcRecord, "245").findFirst().orElse(null));
        TextParserHelper.removeTrailingSlashAndSpace(sb);
        if (isEnglish()) {
            sb =  new StringBuilder(TextParserHelper.toTitleCase(sb.toString()));
        }
        String edition = getSubfieldData(this.marcRecord, "250", "a").collect(Collectors.joining(". "));
        if (!edition.isEmpty()) {
            sb.append(". ").append(edition);
            TextParserHelper.removeTrailingSlashAndSpace(sb);
        }
        return sb.toString();
    }

    @Override
    public Collection<String> getTypes() {
        if (this.types == null) {
            this.types = TypeFactory.getTypes(this.marcRecord);
        }
        return new HashSet<>(this.types);
    }

    @Override
    public List<Version> getVersions() {
        if (this.versions == null) {
            Collection<Version> versionSet = new TreeSet<>(COMPARATOR);
            for (Record holding : this.holdings) {
                Version version = createVersion(holding);
                if (!version.getLinks().isEmpty()) {
                    versionSet.add(version);
                }
            }
            this.versions = Collections.unmodifiableList(new ArrayList<>(versionSet));
        }
        return new ArrayList<>(this.versions);
    }

    @Override
    public int getYear() {
        return MARCRecordSupport.getYear(this.marcRecord);
    }

    @Override
    public boolean isEnglish() {
        String field008 = getFields(this.marcRecord, "008").map(Field::getData).findFirst()
                .orElse(EresourceConstants.EMPTY_008);
        String lang = "";
        if (field008.length() > F008_38) {
            lang = field008.substring(F008_35, F008_38).toLowerCase(Locale.US);
        }
        return "eng".equals(lang) || ("mul".equals(lang) && getPublicationLanguages().contains("English"));
    }

    private String replaceTOCLinebreaks(final String desc) {
        String d = desc;
        if (null != d) {
            for (Pattern pattern : TOC_LINEBREAK_PATTERNS) {
                if (pattern.matcher(desc).find()) {
                    d = pattern.matcher(desc).replaceAll(BR);
                    return d;
                }
            }
        }
        return d;
    }

    protected Version createVersion(final Record holdingRecord) {
        return new MarcVersion(holdingRecord, this.marcRecord, this, this.locationsService);
    }

    protected StringBuilder getTitleStringBuilder(final Field titleField) {
        StringBuilder sb = new StringBuilder();
        if (null != titleField) {
            titleField.getSubfields().stream().filter((final Subfield s) -> "abfknpq".indexOf(s.getCode()) > -1)
                    .forEach((final Subfield s) -> {
                        String data = s.getData();
                        if ('b' == s.getCode()) {
                            if (sb.indexOf(":") != sb.length() - 1) {
                                sb.append(" :");
                            }
                            data = SPACE_SLASH.matcher(data).replaceFirst("");
                        }
                        if (sb.length() > 0) {
                            sb.append(' ');
                        }
                        sb.append(data);
                        TextParserHelper.removeTrailingSlashAndSpace(sb);
                    });
        }
        return sb;
    }
}
