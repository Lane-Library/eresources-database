package edu.stanford.irt.eresources.marc.sul;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.ISBNValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.marc.KeywordsStrategy;
import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.irt.eresources.marc.RecordCollectionFactory;
import edu.stanford.irt.eresources.pmc.PmcDedupAugmentation;
import edu.stanford.lane.catalog.FolioRecord;
import edu.stanford.lane.catalog.FolioRecordCollection;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;
import edu.stanford.lane.catalog.TextHelper;
import edu.stanford.lane.lcsh.LcshMapManager;

public class SulMARCRecordEresourceProcessor extends AbstractEresourceProcessor {

    private static final Pattern BEGINS_HTTPS_OR_ENDS_SLASH = Pattern.compile("(^https?://)|(/$)");

    private static final int F008_DATES_BEGIN = 7;

    private static final int F008_DATES_END = 15;

    private static final Logger log = LoggerFactory.getLogger(SulMARCRecordEresourceProcessor.class);

    private static final Pattern NOT_ALPHANUM_OR_SPACE = Pattern.compile("[^a-zA-Z_0-9 ]");

    private EresourceHandler eresourceHandler;

    private List<InclusionStrategy> inclusionStrategies;

    private KeywordsStrategy keywordsStrategy;

    private LaneDedupAugmentation laneDedupAugmentation;

    private LcshMapManager lcshMapManager = new LcshMapManager();

    private PmcDedupAugmentation pmcDedupAugmentation;

    private RecordCollectionFactory recordCollectionFactory;

    public SulMARCRecordEresourceProcessor(final EresourceHandler eresourceHandler,
            final KeywordsStrategy keywordsStrategy, final RecordCollectionFactory recordCollectionFactory,
            final LaneDedupAugmentation laneDedupAugmentation, final PmcDedupAugmentation pmcDedupAugmentation,
            final List<InclusionStrategy> inclusionStrategies) {
        this.eresourceHandler = eresourceHandler;
        this.keywordsStrategy = keywordsStrategy;
        this.recordCollectionFactory = recordCollectionFactory;
        this.laneDedupAugmentation = laneDedupAugmentation;
        this.pmcDedupAugmentation = pmcDedupAugmentation;
        this.inclusionStrategies = inclusionStrategies;
    }

    @Override
    public void process() {
        FolioRecordCollection recordCollection = this.recordCollectionFactory.newFolioRecordCollection(getStartTime());
        while (recordCollection.hasNext()) {
            FolioRecord folioRecord = recordCollection.next();
            Record marcRecord = folioRecord.getInstanceMarc();
            if (null == marcRecord) {
                log.info("dropping non-marc record: {}", folioRecord);
            }
            // retain inclusion checks here b/c MetaDB inclusion
            // (catalog-service getSulUpdates.sql) does not include strategies
            // like digital book keywords and fiction
            if (null != marcRecord && isInScope(marcRecord) && !isLaneDuplicate(marcRecord)) {
                this.eresourceHandler
                        .handleEresource(new SulMarcEresource(marcRecord, this.keywordsStrategy, this.lcshMapManager));
            }
        }
    }

    private boolean isInScope(final Record marcRecord) {
        return this.inclusionStrategies.stream().anyMatch((final InclusionStrategy is) -> is.isAcceptable(marcRecord));
    }

    private boolean isLaneDuplicate(final Record marcRecord) {
        // LANECAT-776, LANECAT-872: presence of a 909 in SUL records triggers
        // inclusion and skips deduplication
        if (MARCRecordSupport.getFields(marcRecord, "909").count() > 0) {
            return false;
        }
        Set<String> keys = new HashSet<>();
        keys.add(LaneDedupAugmentation.KEY_CATKEY + LaneDedupAugmentation.SEPARATOR
                + MARCRecordSupport.getRecordId(marcRecord));
        for (String lccn : MARCRecordSupport.getSubfieldData(marcRecord, "010", "a").map(String::trim)
                .collect(Collectors.toSet())) {
            keys.add(LaneDedupAugmentation.KEY_LC_CONTROL_NUMBER + LaneDedupAugmentation.SEPARATOR + lccn);
        }
        for (String isbn : MARCRecordSupport.getSubfieldData(marcRecord, "020", "a").map(String::trim)
                .map(TextHelper::cleanIsxn).filter((final String s) -> !s.isEmpty()).collect(Collectors.toSet())) {
            keys.add(LaneDedupAugmentation.KEY_ISBN + LaneDedupAugmentation.SEPARATOR + isbn);
            if (isbn.length() == 10) {
                try {
                    keys.add(LaneDedupAugmentation.KEY_ISBN + LaneDedupAugmentation.SEPARATOR
                            + ISBNValidator.getInstance().convertToISBN13(isbn));
                } catch (IllegalArgumentException e) {
                    log.warn("invalid ISBN: {}", isbn);
                }
            }
        }
        for (String issn : MARCRecordSupport.getSubfieldData(marcRecord, "022", "a").map(String::trim)
                .map(TextHelper::cleanIsxn).filter((final String s) -> !s.isEmpty()).collect(Collectors.toSet())) {
            keys.add(LaneDedupAugmentation.KEY_ISSN + LaneDedupAugmentation.SEPARATOR + issn);
        }
        Set<String> ocolcs = MARCRecordSupport.getSubfieldData(marcRecord, "035", "a")
                .filter((final String s) -> s.startsWith("(OCoLC"))
                .map((final String s) -> s.substring(s.indexOf(')') + 1, s.length())).collect(Collectors.toSet());
        for (String ocolc : ocolcs) {
            keys.add(LaneDedupAugmentation.KEY_OCLC_CONTROL_NUMBER + LaneDedupAugmentation.SEPARATOR + ocolc);
        }
        Set<String> urls = MARCRecordSupport.getSubfieldData(marcRecord, "856", "u")
                .map((final String s) -> s.replace("https://stanford.idm.oclc.org/login?url=", "")).map(String::trim)
                .map((final String s) -> BEGINS_HTTPS_OR_ENDS_SLASH.matcher(s).replaceAll(""))
                .collect(Collectors.toSet());
        for (String url : urls) {
            keys.add(LaneDedupAugmentation.KEY_URL + LaneDedupAugmentation.SEPARATOR + url);
        }
        String title = NOT_ALPHANUM_OR_SPACE.matcher(
                MARCRecordSupport.getSubfieldData(marcRecord, "245", "a").findFirst().map(String::trim).orElse(""))
                .replaceAll("");
        String dates = MARCRecordSupport.getFields(marcRecord, "008").map(Field::getData).findFirst()
                .map((final String s) -> s.substring(F008_DATES_BEGIN, F008_DATES_END)).orElse("00000000");
        StringBuilder sb = new StringBuilder(title);
        sb.append(dates);
        keys.add(LaneDedupAugmentation.KEY_TITLE_DATE + LaneDedupAugmentation.SEPARATOR + sb.toString());
        Set<String> dnlms = MARCRecordSupport.getSubfieldData(MARCRecordSupport.getFields(marcRecord, "016")
                .filter((final Field f) -> f.getIndicator1() == '7').filter((final Field f) -> {
                    Subfield s2 = f.getSubfields().stream().filter((final Subfield s) -> s.getCode() == '2').findFirst()
                            .orElse(null);
                    Subfield sa = f.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'a').findFirst()
                            .orElse(null);
                    return s2 != null && sa != null && "DNLM".equalsIgnoreCase(s2.getData());
                }), "a").collect(Collectors.toSet());
        for (String dnlm : dnlms) {
            keys.add(LaneDedupAugmentation.KEY_DNLM_CONTROL_NUMBER + LaneDedupAugmentation.SEPARATOR + dnlm);
        }
        for (String entry : keys) {
            if (this.laneDedupAugmentation.isDuplicate(entry) || this.pmcDedupAugmentation.isDuplicate(entry)) {
                return true;
            }
        }
        return false;
    }
}
