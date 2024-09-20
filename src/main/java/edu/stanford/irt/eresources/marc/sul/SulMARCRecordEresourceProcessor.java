package edu.stanford.irt.eresources.marc.sul;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.marc.KeywordsStrategy;
import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.irt.eresources.marc.RecordCollectionFactory;
import edu.stanford.irt.eresources.marc.dedup.CatkeyExtractionStrategy;
import edu.stanford.irt.eresources.marc.dedup.DnlmExtractionStrategy;
import edu.stanford.irt.eresources.marc.dedup.IsbnExtractionStrategy;
import edu.stanford.irt.eresources.marc.dedup.IssnExtractionStrategy;
import edu.stanford.irt.eresources.marc.dedup.KeyExtractionStrategy;
import edu.stanford.irt.eresources.marc.dedup.LccnExtractionStrategy;
import edu.stanford.irt.eresources.marc.dedup.OclcExtractionStrategy;
import edu.stanford.irt.eresources.marc.dedup.TitleDateExtractionStrategy;
import edu.stanford.irt.eresources.marc.dedup.UrlExtractionStrategy;
import edu.stanford.irt.eresources.pmc.PmcDedupAugmentation;
import edu.stanford.lane.catalog.FolioRecord;
import edu.stanford.lane.catalog.FolioRecordCollection;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.lcsh.LcshMapManager;

public class SulMARCRecordEresourceProcessor extends AbstractEresourceProcessor {

    private static final Logger log = LoggerFactory.getLogger(SulMARCRecordEresourceProcessor.class);

    private EresourceHandler eresourceHandler;

    private List<KeyExtractionStrategy> deduplicationStrategies;

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
        this.deduplicationStrategies = Arrays.asList(
            new CatkeyExtractionStrategy(),
            new LccnExtractionStrategy(), 
            new IsbnExtractionStrategy(), 
            new IssnExtractionStrategy(),
            new OclcExtractionStrategy(), 
            new UrlExtractionStrategy(), 
            new TitleDateExtractionStrategy(),
            new DnlmExtractionStrategy()
        );
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
        for (KeyExtractionStrategy strategy : this.deduplicationStrategies) {
            for (String key : strategy.extractKeys(marcRecord)) {
                if (this.laneDedupAugmentation.isDuplicate(key) || this.pmcDedupAugmentation.isDuplicate(key)) {
                    return true;
                }
            }
        }
        return false;
    }
}
