package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.RecordCollection;

public class SulMARCRecordEresourceProcessor extends AbstractEresourceProcessor {

    private static final Pattern BEGINS_HTTPS_OR_ENDS_SLASH = Pattern.compile("(^https?://|/$)");

    private static final int F008_DATES_BEGIN = 7;

    private static final int F008_DATES_END = 15;

    private static final Pattern NOT_ALPHANUM_OR_SPACE = Pattern.compile("[^a-zA-Z_0-9 ]");

    private List<String> acceptableDBCallNumbers;

    private List<String> acceptableLCCallNumberPrefixes;

    private EresourceHandler eresourceHandler;

    private KeywordsStrategy keywordsStrategy;

    private LaneDedupAugmentation laneDedupAugmentation;

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
                this.eresourceHandler
                        .handleEresource(new SulMarcEresource(record, this.keywordsStrategy, this.typeFactory));
            }
        }
    }

    private boolean hasAcceptableDBCallNumber(final Record record) {
        return MARCRecordSupport.getSubfieldData(record, "099", "a").anyMatch(this.acceptableDBCallNumbers::contains);
    }

    private boolean hasAcceptableLCCallNumberPrefix(final Record record) {
        List<String> cns = MARCRecordSupport.getSubfieldData(record, "050", "a").collect(Collectors.toList());
        for (String cn : cns) {
            for (String lccn : this.acceptableLCCallNumberPrefixes) {
                if (cn.startsWith(lccn)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasNLMCallNumber(final Record record) {
        return MARCRecordSupport.getFields(record, "060").findAny().isPresent();
    }

    private boolean isInScope(final Record record) {
        return (hasNLMCallNumber(record) || hasAcceptableLCCallNumberPrefix(record)
                || hasAcceptableDBCallNumber(record));
    }

    private boolean isLane(final Record record) {
        return MARCRecordSupport.getSubfieldData(record, "099", "m")
                .anyMatch((final String s) -> s.equalsIgnoreCase("LANE-MED"));
    }

    private boolean isLaneDuplicate(final Record record) {
        Map<String, String> keys = new HashMap<>();
        keys.put(LaneDedupAugmentation.KEY_CATKEY, Integer.toString(MARCRecordSupport.getRecordId(record)));
        for (String lccn : MARCRecordSupport.getSubfieldData(record, "010", "a").map(String::trim)
                .collect(Collectors.toSet())) {
            keys.put(LaneDedupAugmentation.KEY_LC_CONTROL_NUMBER, lccn);
        }
        for (String isbn : MARCRecordSupport.getSubfieldData(record, "020").map(String::trim)
                .map(TextParserHelper::cleanIsxn).collect(Collectors.toSet())) {
            keys.put(LaneDedupAugmentation.KEY_ISBN, isbn);
        }
        for (String issn : MARCRecordSupport.getSubfieldData(record, "022").map(String::trim)
                .map(TextParserHelper::cleanIsxn).collect(Collectors.toSet())) {
            keys.put(LaneDedupAugmentation.KEY_ISSN, issn);
        }
        Set<String> ocolcs = MARCRecordSupport.getSubfieldData(record, "035", "a")
                .filter((final String s) -> s.startsWith("(OCoLC"))
                .map((final String s) -> s.substring(s.indexOf(')') + 1, s.length())).collect(Collectors.toSet());
        for (String ocolc : ocolcs) {
            keys.put(LaneDedupAugmentation.KEY_OCLC_CONTROL_NUMBER, ocolc);
        }
        Set<String> urls = MARCRecordSupport.getSubfieldData(record, "856", "u")
                .map((final String s) -> s.replace("https://stanford.idm.oclc.org/login?url=", "")).map(String::trim)
                .map((final String s) -> BEGINS_HTTPS_OR_ENDS_SLASH.matcher(s).replaceAll(""))
                .collect(Collectors.toSet());
        for (String url : urls) {
            keys.put(LaneDedupAugmentation.KEY_URL, url);
        }
        String title = NOT_ALPHANUM_OR_SPACE
                .matcher(MARCRecordSupport.getSubfieldData(record, "245", "a").findFirst().map(String::trim).orElse(""))
                .replaceAll("");
        String dates = MARCRecordSupport.getFields(record, "008").map(Field::getData).findFirst()
                .map((final String s) -> s.substring(F008_DATES_BEGIN, F008_DATES_END)).orElse("00000000");
        StringBuilder sb = new StringBuilder(title);
        sb.append(dates);
        keys.put(LaneDedupAugmentation.KEY_TITLE_DATE, sb.toString());
        for (Entry<String, String> entry : keys.entrySet()) {
            if (this.laneDedupAugmentation.isDuplicate(entry.getKey(), entry.getValue())) {
                return true;
            }
        }
        return false;
    }
}
