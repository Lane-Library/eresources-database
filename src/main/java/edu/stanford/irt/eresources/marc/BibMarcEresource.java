package edu.stanford.irt.eresources.marc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.ItemCount;
import edu.stanford.irt.eresources.LanguageMap;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.VersionComparator;
import edu.stanford.irt.eresources.sax.DateParser;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

/**
 * An Eresource that encapsulates the marc Records from which it is derived.
 */
public class BibMarcEresource extends MARCRecordSupport implements Eresource {

    public static final int THIS_YEAR = LocalDate.now(ZoneId.of("America/Los_Angeles")).getYear();

    private static final Pattern ACCEPTED_YEAR_PATTERN = Pattern.compile("^\\d[\\d|u]{3}$");

    private static final Pattern COLON_OR_SEMICOLON = Pattern.compile("(:|;)");

    private static final Pattern COMMA_DOLLAR = Pattern.compile(",$");

    private static final Comparator<Version> COMPARATOR = new VersionComparator();

    private static final LanguageMap LANGUAGE_MAP = new LanguageMap();

    private static final String SEMICOLON_SPACE = "; ";

    private static final Pattern SPACE_SLASH = Pattern.compile(" /");

    private static final Pattern WILD_SPACE_WORD_PERIOD = Pattern.compile(".* \\w\\.");

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private List<Record> holdings;

    private ItemCount itemCount;

    private KeywordsStrategy keywordsStrategy;

    private String primaryType;

    private Record record;

    private TypeFactory typeFactory;

    private Collection<String> types;

    private List<Version> versions;

    public BibMarcEresource(final List<Record> recordList, final KeywordsStrategy keywordsStrategy,
            final ItemCount itemCount, final TypeFactory typeFactory) {
        this.record = recordList.get(0);
        this.holdings = recordList.subList(1, recordList.size());
        this.keywordsStrategy = keywordsStrategy;
        this.itemCount = itemCount;
        this.typeFactory = typeFactory;
    }

    @Override
    public Collection<String> getAbbreviatedTitles() {
        return getSubfieldData(getFields(this.record, "246").filter((final Field f) -> {
            Subfield i = f.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'i').findFirst()
                    .orElse(null);
            Subfield a = f.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'a').findFirst()
                    .orElse(null);
            return i != null && a != null && "Acronym/initialism:".equalsIgnoreCase(i.getData());
        }), "a").collect(Collectors.toSet());
    }

    @Override
    public Collection<String> getAlternativeTitles() {
        return getSubfieldData(this.record, "130|210|246|247", "a").collect(Collectors.toSet());
    }

    @Override
    public Collection<String> getBroadMeshTerms() {
        return getSubfieldData(getFields(this.record, "650")
                .filter((final Field f) -> f.getIndicator1() == '4' && f.getIndicator2() == '2'), "a")
                        .map(this::maybeStripTrailingPeriod).collect(Collectors.toSet());
    }

    @Override
    public String getDate() {
        String date = null;
        List<Field> fields773 = getFields(this.record, "773").collect(Collectors.toList());
        int subfieldWCount = 0;
        for (int i = 0; i < fields773.size(); i++) {
            List<Subfield> subfields = fields773.get(i).getSubfields();
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
            String field008 = getFields(this.record, "008").map(Field::getData).findFirst().orElse("");
            String endDate = parseYear(field008.substring(11, 15));
            String beginDate = parseYear(field008.substring(7, 11));
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
        StringBuilder sb = new StringBuilder();
        getSubfieldData(this.record, "520").forEach((final String s) -> {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(s);
        });
        if (sb.length() == 0) {
            getSubfieldData(this.record, "505").forEach((final String s) -> {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(s);
            });
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    @Override
    public String getId() {
        return getRecordType() + "-" + getRecordId();
    }

    @Override
    public int[] getItemCount() {
        return this.itemCount.itemCount(getRecordId());
    }

    @Override
    public String getKeywords() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.keywordsStrategy.getKeywords(this.record));
        this.holdings.stream().forEach((final Record holding) -> sb.append(this.keywordsStrategy.getKeywords(holding)));
        return sb.toString();
    }

    @Override
    public Collection<String> getMeshTerms() {
        return getSubfieldData(getFields(this.record, "650|651")
                .filter((final Field f) -> ("650".equals(f.getTag()) && "2356".indexOf(f.getIndicator2()) > -1)
                        || ("651".equals(f.getTag()) && f.getIndicator2() == '7')),
                "a").map(this::maybeStripTrailingPeriod).collect(Collectors.toSet());
    }

    @Override
    public String getPrimaryType() {
        if (this.primaryType == null) {
            this.primaryType = this.typeFactory.getPrimaryType(this.record);
        }
        return this.primaryType;
    }

    @Override
    public Collection<String> getPublicationAuthors() {
        return Collections
                .unmodifiableCollection(
                        getSubfieldData(
                                getFields(this.record, "100|700").filter((final Field f) -> "100".equals(f.getTag())
                                        || ("700".equals(f.getTag()) && !(getPrimaryType().startsWith("Journal")))),
                                "a").map((final String s) -> COMMA_DOLLAR.matcher(s).replaceFirst(""))
                                        .map((final String auth) -> auth.endsWith(".")
                                                && !WILD_SPACE_WORD_PERIOD.matcher(auth).matches()
                                                        ? auth.substring(0, auth.length() - 1)
                                                        : auth)
                                        .collect(Collectors.toList()));
    }

    @Override
    public String getPublicationAuthorsText() {
        String authorsText = getSubfieldData(this.record, "245", "c")
                // get the last c
                .reduce((final String a, final String b) -> b).orElse(null);
        if (authorsText == null) {
            StringBuilder sb = new StringBuilder();
            for (String auth : getPublicationAuthors()) {
                sb.append(auth).append(SEMICOLON_SPACE);
            }
            if (sb.toString().endsWith(SEMICOLON_SPACE)) {
                sb.delete(sb.length() - 2, sb.length());
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
        String field008 = getFields(this.record, "008").map(Field::getData).findFirst().orElse("");
        String lang = field008.substring(35, 38);
        languages.add(LANGUAGE_MAP.getLanguage(lang.toLowerCase(Locale.US)));
        languages.addAll(getSubfieldData(this.record, "041").map(String::toLowerCase).map(LANGUAGE_MAP::getLanguage)
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
        List<Field> fields773 = getFields(this.record, "773").collect(Collectors.toList());
        int subfieldWCount = 0;
        for (int i = 0; i < fields773.size(); i++) {
            Field field733 = fields773.get(i);
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
            if (field733.getSubfields().stream().anyMatch((final Subfield s) -> s.getCode() == 'w')) {
                subfieldWCount++;
            }
        }
        return sb.toString();
    }

    @Override
    public String getPublicationTitle() {
        List<Field> fields773 = getFields(this.record, "773").collect(Collectors.toList());
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
    public int getRecordId() {
        return Integer.parseInt(getFields(this.record, "001").map(Field::getData).findFirst().orElse("0"));
    }

    @Override
    public String getRecordType() {
        return "bib";
    }

    @Override
    public String getShortTitle() {
        return getSubfieldData(this.record, "149", "a").findFirst().orElse(null);
    }

    @Override
    public String getSortTitle() {
        StringBuilder sb = getStringBuilderWith245();
        int offset = getFields(this.record, "245").map((final Field f) -> Integer.valueOf(f.getIndicator2()) - 48)
                .findFirst().orElse(0);
        return sb.substring(offset);
    }

    @Override
    public String getTitle() {
        StringBuilder sb = getStringBuilderWith245();
        removeTrailingSlashAndSpace(sb);
        String edition = getSubfieldData(this.record, "250", "a").collect(Collectors.joining(". "));
        if (!edition.isEmpty()) {
            sb.append(". ").append(edition);
            removeTrailingSlashAndSpace(sb);
        }
        return sb.toString();
    }

    @Override
    public Collection<String> getTypes() {
        if (this.types == null) {
            this.types = this.typeFactory.getTypes(this.record);
        }
        return this.types;
    }

    @Override
    public Date getUpdated() {
        try {
            Date updated = this.dateFormat
                    .parse(getFields(this.record, "005").map(Field::getData).findFirst().orElse(null));
            for (Record holding : this.holdings) {
                Date holdingUpdated = this.dateFormat
                        .parse(getFields(holding, "005").map(Field::getData).findFirst().orElse(null));
                if (holdingUpdated.compareTo(updated) > 0) {
                    updated = holdingUpdated;
                }
            }
            return updated;
        } catch (ParseException e) {
            throw new EresourceDatabaseException(e);
        }
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
        return this.versions;
    }

    @Override
    public int getYear() {
        int year = 0;
        String dateField = getFields(this.record, "008").map(Field::getData).findFirst().orElse("0000000000000000");
        String endDate = parseYear(dateField.substring(11, 15));
        if (endDate != null) {
            year = Integer.parseInt(endDate);
        } else {
            String beginDate = parseYear(dateField.substring(7, 11));
            if (beginDate != null) {
                year = Integer.parseInt(beginDate);
            }
        }
        return year;
    }

    @Override
    public boolean isClone() {
        return false;
    }

    @Override
    public boolean isCore() {
        return getSubfieldData(this.record, "655", "a").anyMatch("core material"::equalsIgnoreCase);
    }

    @Override
    public boolean isEnglish() {
        return getPublicationLanguages().contains("English");
    }

    @Override
    public boolean isLaneConnex() {
        return getSubfieldData(this.record, "655", "a").anyMatch("laneconnex"::equalsIgnoreCase);
    }

    protected Version createVersion(final Record record) {
        return new MarcVersion(record, this.record, this);
    }

    private StringBuilder getStringBuilderWith245() {
        StringBuilder sb = new StringBuilder();
        getFields(this.record, "245").findFirst().ifPresent((final Field f) -> f.getSubfields().stream()
                .filter((final Subfield s) -> "abnpq".indexOf(s.getCode()) > -1).forEach((final Subfield s) -> {
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
                    removeTrailingSlashAndSpace(sb);
                }));
        return sb;
    }

    // remove trailing periods, some probably should have them but
    // voyager puts them on everything :-(
    private String maybeStripTrailingPeriod(final String string) {
        int lastPeriod = string.lastIndexOf('.');
        if (lastPeriod >= 0) {
            int lastPosition = string.length() - 1;
            if (lastPeriod == lastPosition) {
                return string.substring(0, lastPosition);
            }
        }
        return string;
    }

    private String parseYear(final String year) {
        String parsedYear = null;
        Matcher yearMatcher = ACCEPTED_YEAR_PATTERN.matcher(year);
        if (yearMatcher.matches()) {
            parsedYear = year;
            if ("9999".equals(year)) {
                parsedYear = Integer.toString(THIS_YEAR);
            } else if (year.contains("u")) {
                int estimate = Integer.parseInt(year.replace('u', '5'));
                if (estimate > THIS_YEAR) {
                    estimate = THIS_YEAR;
                }
                parsedYear = Integer.toString(estimate);
            }
        }
        return parsedYear;
    }

    private void removeTrailingSlashAndSpace(final StringBuilder sb) {
        while (sb.lastIndexOf("/") == sb.length() - 1 || sb.lastIndexOf(" ") == sb.length() - 1) {
            sb.setLength(sb.length() - 1);
        }
    }
}
