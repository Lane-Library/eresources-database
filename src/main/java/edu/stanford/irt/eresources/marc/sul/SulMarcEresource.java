package edu.stanford.irt.eresources.marc.sul;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.DateParser;
import edu.stanford.irt.eresources.EresourceConstants;
import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.marc.AbstractMarcEresource;
import edu.stanford.irt.eresources.marc.KeywordsStrategy;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;
import edu.stanford.lane.lcsh.LcshMapManager;

public class SulMarcEresource extends AbstractMarcEresource {

    private static final String BR = "<br/>";

    private static final Logger log = LoggerFactory.getLogger(SulMarcEresource.class);

    private static final int MAX_YEAR = TextParserHelper.THIS_YEAR + 5;

    private static final int MIN_YEAR = 500;

    // patterns from SUL's indexer
    // https://github.com/sul-dlss/searchworks_traject_indexer/blob/3efc73bbfed80e31520481fba059dda063770463/lib/traject/config/sirsi_config.rb#L2004
    private static final Pattern[] TOC_LINEBREAK_PATTERNS = { Pattern.compile("[^\\S]--[^\\S]"),
            Pattern.compile(" {5}+"), Pattern.compile("--[^\\S]"), Pattern.compile("[^\\S]\\.-[^\\S]"),
            Pattern.compile("(?=(?:Chapter|Section|Appendix|Part|v\\.) \\d+[:\\.-]?\\s+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?=(?:Appendix|Section|Chapter) [XVI]+[\\.-]?)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?=[^\\d]\\d+[:\\.-]\\s+)"), Pattern.compile("(?=\\s{2,}\\d+\\s+)") };

    private static final int YEAR_LENGTH = 4;

    private LcshMapManager lcshMapManager;

    private SulTypeFactory sulTypeFactory;

    private int year;

    public SulMarcEresource(final Record marcRecord, final KeywordsStrategy keywordsStrategy,
            final SulTypeFactory typeFactory, final LcshMapManager lcshMapManager) {
        this.marcRecord = marcRecord;
        this.keywordsStrategy = keywordsStrategy;
        this.sulTypeFactory = typeFactory;
        this.lcshMapManager = lcshMapManager;
    }

    // "Also known as" includes abbreviations as well as alternate titles
    // only include 246 a when all caps
    @Override
    public Collection<String> getAbbreviatedTitles() {
        return getSubfieldData(getFields(this.marcRecord, "246").filter((final Field f) -> {
            Subfield i = f.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'i').findFirst()
                    .orElse(null);
            Subfield a = f.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'a').findFirst()
                    .orElse(null);
            return i != null && a != null && "Also known as:".equalsIgnoreCase(i.getData());
        }), "a").filter(this::isAllCaps).collect(Collectors.toSet());
    }

    @Override
    public String getDate() {
        return DateParser.parseDate(Integer.toString(getYear()));
    }

    @Override
    public String getDescription() {
        // prefer 905s over 505s and 920s over 520s
        // 905/505 get linebreak parsing to improve formatting
        String tag = getFields(this.marcRecord, "920").count() > 0 ? "920" : "520";
        final String labelSummary = getFields(this.marcRecord, "520|920").count() > 0 ? "Summary:" + BR : "";
        StringBuilder sb = new StringBuilder(labelSummary);
        getSubfieldData(this.marcRecord, tag, "ab").forEach((final String s) -> {
            if (sb.length() > labelSummary.length()) {
                sb.append(' ');
            }
            sb.append(s);
        });
        tag = getFields(this.marcRecord, "905").count() > 0 ? "905" : "505";
        final String labelContents = getFields(this.marcRecord, "505|905").count() > 0 ? "Contents:" + BR : "";
        // add breaks before the contents label if a summary is already present
        if (sb.length() > labelSummary.length() && !labelContents.isEmpty()) {
            sb.append(BR);
            sb.append(BR);
        }
        sb.append(labelContents);
        getSubfieldData(this.marcRecord, tag, "a").forEach((final String s) -> {
            if (sb.length() > labelContents.length()) {
                sb.append(' ');
            }
            sb.append(replaceTOCLinebreaks(s));
        });
        return sb.length() > 0 ? sb.toString() : null;
    }

    @Override
    public String getKeywords() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(this.keywordsStrategy.getKeywords(this.marcRecord));
        } catch (ArrayIndexOutOfBoundsException e) {
            // errors expected on a handful of SUL records
            log.info("problem extracting keywords from {}, {}", this.marcRecord, e.getMessage());
        }
        return sb.toString();
    }

    @Override
    public Collection<String> getMeshTerms() {
        Collection<String> mesh = getSubfieldData(
                getFields(this.marcRecord, "650").filter((final Field f) -> ("2".indexOf(f.getIndicator2()) > -1)), "a")
                        .map(TextParserHelper::maybeStripTrailingPeriod).collect(Collectors.toSet());
        MARCRecordSupport.getFields(this.marcRecord, "650")
                .filter((final Field f) -> ("0".indexOf(f.getIndicator2()) > -1)).forEach((final Field f) -> {
                    StringBuilder sb = new StringBuilder();
                    f.getSubfields().stream().filter((final Subfield sf) -> "ax".indexOf(sf.getCode()) > -1)
                            .forEach((final Subfield sf) -> {
                                if ('x' == sf.getCode()) {
                                    sb.append("--");
                                }
                                sb.append(TextParserHelper.maybeStripTrailingPeriod(sf.getData()));
                            });
                    mesh.addAll(this.lcshMapManager.getMeshForHeading(sb.toString()));
                });
        return mesh;
    }

    @Override
    public String getPrimaryType() {
        if (this.primaryType == null) {
            this.primaryType = this.sulTypeFactory.getPrimaryType(this.marcRecord);
        }
        return this.primaryType;
    }

    @Override
    public String getRecordId() {
        return MARCRecordSupport.getRecordId(this.marcRecord);
    }

    @Override
    public String getRecordType() {
        return "sul";
    }

    @Override
    public String getShortTitle() {
        String title = getSubfieldData(this.marcRecord, "222", "a").findFirst().orElse(null);
        if (null == title) {
            title = getSubfieldData(this.marcRecord, "245", "a").findFirst().orElse(null);
        }
        return title;
    }

    @Override
    public String getTitle() {
        StringBuilder sb = new StringBuilder(super.getTitle());
        // LANEWEB-10639: a few sul records have "<>" to indicate linked 880 title fields
        String titleLinkage = getSubfieldData(this.marcRecord, "245", "6").findFirst().orElse(null);
        if (sb.toString().startsWith("<>") && null != titleLinkage && titleLinkage.startsWith("880-")) {
            getFields(this.marcRecord, "880").findAny()
                    .ifPresent((final Field f) -> f.getSubfields().stream()
                            .filter((final Subfield s) -> '6' == s.getCode() && s.getData().startsWith("245"))
                            .forEach((final Subfield s) -> {
                                sb.setLength(0);
                                sb.append(getTitleStringBuilder(f));
                            }));
        }
        return sb.toString();
    }

    @Override
    public Collection<String> getTypes() {
        if (this.types == null) {
            this.types = this.sulTypeFactory.getTypes(this.marcRecord);
        }
        if (!EresourceConstants.OTHER.equals(getPrimaryType()) && !this.types.contains(getPrimaryType())) {
            this.types.add(getPrimaryType());
        }
        return new ArrayList<>(this.types);
    }

    @Override
    public LocalDateTime getUpdated() {
        try {
            return LocalDateTime.parse(getFields(this.marcRecord, "005").map(Field::getData).findFirst().orElse(null),
                    FORMATTER);
        } catch (DateTimeParseException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    @Override
    public List<Version> getVersions() {
        if (this.versions == null) {
            Collection<Version> versionSet = new TreeSet<>(COMPARATOR);
            Version version = createVersion(this.marcRecord);
            if (!version.getLinks().isEmpty()) {
                versionSet.add(version);
            }
            this.versions = Collections.unmodifiableList(new ArrayList<>(versionSet));
        }
        return new ArrayList<>(this.versions);
    }

    @Override
    public int getYear() {
        if (this.year != 0) {
            return this.year;
        }
        int yr = MARCRecordSupport.getYear(this.marcRecord);
        // SUL 008s are sometimes really off; fetch 264c/260c dates as needed
        if (yr < MIN_YEAR || yr >= MAX_YEAR) {
            String date = DateParser.parseYear(getSubfieldData(
                    getFields(this.marcRecord, "264").filter((final Field f) -> f.getIndicator2() == '1'), "c")
                            .findFirst().orElse(null));
            if (null == date) {
                date = DateParser.parseYear(getSubfieldData(this.marcRecord, "264", "c").findFirst().orElse(null));
            }
            if (null == date) {
                date = DateParser.parseYear(getSubfieldData(this.marcRecord, "260", "c").findFirst().orElse(null));
            }
            if (null != date && date.length() == YEAR_LENGTH) {
                yr = Integer.parseInt(date);
            }
        }
        if (yr >= MIN_YEAR && yr <= MAX_YEAR) {
            this.year = yr;
        }
        return this.year;
    }

    private boolean isAllCaps(final String string) {
        String caps = string.toUpperCase(Locale.US);
        return string.equals(caps);
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

    @Override
    protected Version createVersion(final Record holdingsRecord) {
        return new SulMarcVersion(holdingsRecord, this);
    }
}
