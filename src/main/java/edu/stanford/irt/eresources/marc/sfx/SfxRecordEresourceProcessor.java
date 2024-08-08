package edu.stanford.irt.eresources.marc.sfx;

import java.util.List;

import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.marc.KeywordsStrategy;
import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.RecordCollectionFactory;
import edu.stanford.irt.eresources.marc.sul.InclusionStrategy;
import edu.stanford.irt.eresources.marc.sul.SulMARCRecordEresourceProcessor;
import edu.stanford.irt.eresources.pmc.PmcDedupAugmentation;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.RecordCollection;
import edu.stanford.lane.lcsh.LcshMapManager;

public class SfxRecordEresourceProcessor extends SulMARCRecordEresourceProcessor {

    public SfxRecordEresourceProcessor(EresourceHandler eresourceHandler, KeywordsStrategy keywordsStrategy,
            RecordCollectionFactory recordCollectionFactory, LaneDedupAugmentation laneDedupAugmentation,
            PmcDedupAugmentation pmcDedupAugmentation, List<InclusionStrategy> inclusionStrategies) {
        super(eresourceHandler, keywordsStrategy, recordCollectionFactory, laneDedupAugmentation, pmcDedupAugmentation,
                inclusionStrategies);
        this.recordCollectionFactory = recordCollectionFactory;
        this.eresourceHandler = eresourceHandler;
        this.keywordsStrategy = keywordsStrategy;

    }

    private RecordCollectionFactory recordCollectionFactory;

    private EresourceHandler eresourceHandler;

    private LcshMapManager lcshMapManager = new LcshMapManager();

    private KeywordsStrategy keywordsStrategy;

    @Override
    public void process() {
        RecordCollection rc = this.recordCollectionFactory.newRecordCollection(getStartTime());
        while (rc.hasNext()) {
            Record mr = rc.next();
            if (null != mr && !isDuplicate(mr)) {
                this.eresourceHandler
                        .handleEresource(new SfxMarcEresource(mr, this.keywordsStrategy, this.lcshMapManager));
            }
        }
    }

}
