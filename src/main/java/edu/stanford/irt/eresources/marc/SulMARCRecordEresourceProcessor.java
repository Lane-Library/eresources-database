package edu.stanford.irt.eresources.marc;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.RecordCollection;

public class SulMARCRecordEresourceProcessor extends AbstractEresourceProcessor {

    private static final String OCOLC = "(OCoLC)";

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
        this.acceptableLCCallNumberPrefixes = acceptableLCCallNumberPrefixes;
        this.acceptableDBCallNumbers = acceptableDBCallNumbers;
        this.laneDedupAugmentation = laneDedupAugmentation;
    }

    @Override
    public void process() {
        RecordCollection recordCollection = this.recordCollectionFactory.newRecordCollection(getStartTime());
        while (recordCollection.hasNext()) {
            Record record = recordCollection.next();
            if ((hasAcceptableLCCallNumberPrefix(record) || hasAcceptableDBCallNumber(record)) && !isLane(record)
                    && !isLaneDuplicate(record)) {
                this.eresourceHandler
                        .handleEresource(new SulBibMarcEresource(record, this.keywordsStrategy, this.typeFactory));
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

    private boolean isLane(final Record record) {
        return MARCRecordSupport.getSubfieldData(record, "099", "m")
                .anyMatch((final String s) -> s.equalsIgnoreCase("LANE-MED"));
    }

    private boolean isLaneDuplicate(final Record record) {
        String catkey = MARCRecordSupport.getFields(record, "001").map(Field::getData).findFirst().orElse("0")
                .replaceAll("\\D", "");
        if (this.laneDedupAugmentation.isDuplicate("catkey", catkey)) {
            return true;
        }
        Set<String> lccns = MARCRecordSupport.getSubfieldData(record, "010", "a").map(String::trim)
                .collect(Collectors.toSet());
        for (String lccn : lccns) {
            if (this.laneDedupAugmentation.isDuplicate("lccntrln", lccn)) {
                return true;
            }
        }
        Set<String> ocolcs = MARCRecordSupport.getSubfieldData(record, "035", "a")
                .filter((final String s) -> s.startsWith(OCOLC)).map((final String s) -> s.substring(OCOLC.length()))
                .collect(Collectors.toSet());
        for (String ocolc : ocolcs) {
            if (this.laneDedupAugmentation.isDuplicate("ocolc", ocolc)) {
                return true;
            }
        }
        Set<String> urls = MARCRecordSupport.getSubfieldData(record, "856", "u")
                .map((final String s) -> s.replace("https://stanford.idm.oclc.org/login?url=", "")).map(String::trim)
                .map((final String s) -> s.replaceAll("(^https?://|/$)", "")).collect(Collectors.toSet());
        for (String url : urls) {
            if (this.laneDedupAugmentation.isDuplicate("url", url)) {
                return true;
            }
        }
        String title = MARCRecordSupport.getSubfieldData(record, "245", "a").findFirst().map(String::trim).orElse("")
                .replaceAll("[^a-zA-Z_0-9 ]", "");
        String dates = MARCRecordSupport.getFields(record, "008").map(Field::getData).findFirst()
                .map((final String s) -> s.substring(7, 15)).orElse("00000000");
        StringBuilder sb = new StringBuilder(title);
        sb.append(dates);
        return this.laneDedupAugmentation.isDuplicate("title_date", sb.toString());
    }
}
