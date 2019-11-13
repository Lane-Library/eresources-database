package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;
import edu.stanford.lane.catalog.RecordCollection;
import edu.stanford.lane.lcsh.LcshMapManager;

public class SulMARCRecordEresourceProcessor extends AbstractEresourceProcessor {

    private static final Pattern BEGINS_HTTPS_OR_ENDS_SLASH = Pattern.compile("(^https?://|/$)");

    private static final int F008_DATES_BEGIN = 7;

    private static final int F008_DATES_END = 15;

    private static final Pattern FICTION = Pattern.compile("(^|\\b[^non-])fiction(\\S|\\b)", Pattern.CASE_INSENSITIVE);

    private static final Pattern NOT_ALPHANUM_OR_SPACE = Pattern.compile("[^a-zA-Z_0-9 ]");

    private List<String> acceptableDBCallNumbers;

    private List<String> acceptableLCCallNumberPrefixes;

    private EresourceHandler eresourceHandler;

    private KeywordsStrategy keywordsStrategy;

    private LaneDedupAugmentation laneDedupAugmentation;

    private LcshMapManager lcshMapManager = new LcshMapManager();

    private RecordCollectionFactory recordCollectionFactory;

    private SulTypeFactory typeFactory;

    public SulMARCRecordEresourceProcessor(final EresourceHandler eresourceHandler,
            final KeywordsStrategy keywordsStrategy, final RecordCollectionFactory recordCollectionFactory,
            final SulTypeFactory typeFactory, final List<String> acceptableLCCallNumberPrefixes,
            final List<String> acceptableDBCallNumbers, final LaneDedupAugmentation laneDedupAugmentation) {
        this.eresourceHandler = eresourceHandler;
        this.keywordsStrategy = keywordsStrategy;
        this.recordCollectionFactory = recordCollectionFactory;
        this.typeFactory = typeFactory;
        this.acceptableLCCallNumberPrefixes = new ArrayList<>(acceptableLCCallNumberPrefixes);
        this.acceptableDBCallNumbers = new ArrayList<>(acceptableDBCallNumbers);
        this.laneDedupAugmentation = laneDedupAugmentation;
    }

    @Override
    public void process() {
        RecordCollection recordCollection = this.recordCollectionFactory.newRecordCollection(getStartTime());
        while (recordCollection.hasNext()) {
            Record record = recordCollection.next();
            if (isInScope(record) && !isLane(record) && !isLaneDuplicate(record)) {
                this.eresourceHandler.handleEresource(
                        new SulMarcEresource(record, this.keywordsStrategy, this.typeFactory, this.lcshMapManager));
            }
        }
    }

    private boolean hasAcceptableDBCallNumber(final Record record) {
        return MARCRecordSupport.getSubfieldData(record, "099", "a").anyMatch(this.acceptableDBCallNumbers::contains);
    }

    private boolean hasAcceptableLCCallNumberPrefix(final Record record) {
        Set<String> cns = MARCRecordSupport.getSubfieldData(record, "050|090", "a").collect(Collectors.toSet());
        if (includedInAcceptableLCCallNumberPrefixes(cns)) {
            return true;
        }
        // augment callnumber list with mapped LCSH->callnumber values
        // but skip cn mapping for fiction records
        if (isFiction(record)) {
            return false;
        }
        cns.clear();
        MARCRecordSupport.getFields(record, "650").filter((final Field f) -> ("07".indexOf(f.getIndicator2()) > -1))
                .forEach((final Field f) -> {
                    StringBuilder sb = new StringBuilder();
                    f.getSubfields().stream().filter((final Subfield sf) -> "ax".indexOf(sf.getCode()) > -1)
                            .forEach((final Subfield sf) -> {
                                if ('x' == sf.getCode()) {
                                    sb.append("--");
                                }
                                sb.append(TextParserHelper.maybeStripTrailingPeriod(sf.getData()));
                            });
                    cns.addAll(this.lcshMapManager.getCallnumbersForHeading(sb.toString()));
                });
        return includedInAcceptableLCCallNumberPrefixes(cns);
    }

    private boolean hasNLMCallNumber(final Record record) {
        return MARCRecordSupport.getFields(record, "060").findAny().isPresent();
    }

    private boolean includedInAcceptableLCCallNumberPrefixes(final Set<String> callnumbers) {
        for (String cn : callnumbers) {
            for (String lccn : this.acceptableLCCallNumberPrefixes) {
                if (cn.startsWith(lccn)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isFiction(final Record record) {
        return MARCRecordSupport.getSubfieldData(record, "650", "av")
                .anyMatch((final String s) -> FICTION.matcher(s).find())
                || MARCRecordSupport.getSubfieldData(record, "651", "av")
                        .anyMatch((final String s) -> FICTION.matcher(s).find())
                || MARCRecordSupport.getSubfieldData(record, "655", "av")
                        .anyMatch((final String s) -> FICTION.matcher(s).find());
    }

    private boolean isInScope(final Record record) {
        return (hasNLMCallNumber(record) || hasAcceptableLCCallNumberPrefix(record)
                || hasAcceptableDBCallNumber(record));
    }

    private boolean isLane(final Record record) {
        return MARCRecordSupport.getSubfieldData(record, "999", "m").anyMatch("LANE-MED"::equalsIgnoreCase);
    }

    private boolean isLaneDuplicate(final Record record) {
        Set<String> keys = new HashSet<>();
        keys.add(LaneDedupAugmentation.KEY_CATKEY + LaneDedupAugmentation.SEPARATOR
                + Integer.toString(MARCRecordSupport.getRecordId(record)));
        for (String lccn : MARCRecordSupport.getSubfieldData(record, "010", "a").map(String::trim)
                .collect(Collectors.toSet())) {
            keys.add(LaneDedupAugmentation.KEY_LC_CONTROL_NUMBER + LaneDedupAugmentation.SEPARATOR + lccn);
        }
        for (String isbn : MARCRecordSupport.getSubfieldData(record, "020").map(String::trim)
                .map(TextParserHelper::cleanIsxn).filter((final String s) -> !s.isEmpty())
                .collect(Collectors.toSet())) {
            keys.add(LaneDedupAugmentation.KEY_ISBN + LaneDedupAugmentation.SEPARATOR + isbn);
        }
        for (String issn : MARCRecordSupport.getSubfieldData(record, "022").map(String::trim)
                .map(TextParserHelper::cleanIsxn).filter((final String s) -> !s.isEmpty())
                .collect(Collectors.toSet())) {
            keys.add(LaneDedupAugmentation.KEY_ISSN + LaneDedupAugmentation.SEPARATOR + issn);
        }
        Set<String> ocolcs = MARCRecordSupport.getSubfieldData(record, "035", "a")
                .filter((final String s) -> s.startsWith("(OCoLC"))
                .map((final String s) -> s.substring(s.indexOf(')') + 1, s.length())).collect(Collectors.toSet());
        for (String ocolc : ocolcs) {
            keys.add(LaneDedupAugmentation.KEY_OCLC_CONTROL_NUMBER + LaneDedupAugmentation.SEPARATOR + ocolc);
        }
        Set<String> urls = MARCRecordSupport.getSubfieldData(record, "856", "u")
                .map((final String s) -> s.replace("https://stanford.idm.oclc.org/login?url=", "")).map(String::trim)
                .map((final String s) -> BEGINS_HTTPS_OR_ENDS_SLASH.matcher(s).replaceAll(""))
                .collect(Collectors.toSet());
        for (String url : urls) {
            keys.add(LaneDedupAugmentation.KEY_URL + LaneDedupAugmentation.SEPARATOR + url);
        }
        String title = NOT_ALPHANUM_OR_SPACE
                .matcher(MARCRecordSupport.getSubfieldData(record, "245", "a").findFirst().map(String::trim).orElse(""))
                .replaceAll("");
        String dates = MARCRecordSupport.getFields(record, "008").map(Field::getData).findFirst()
                .map((final String s) -> s.substring(F008_DATES_BEGIN, F008_DATES_END)).orElse("00000000");
        StringBuilder sb = new StringBuilder(title);
        sb.append(dates);
        keys.add(LaneDedupAugmentation.KEY_TITLE_DATE + LaneDedupAugmentation.SEPARATOR + sb.toString());
        for (String entry : keys) {
            if (this.laneDedupAugmentation.isDuplicate(entry)) {
                return true;
            }
        }
        return false;
    }
}
