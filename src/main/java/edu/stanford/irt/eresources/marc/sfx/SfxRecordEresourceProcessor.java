package edu.stanford.irt.eresources.marc.sfx;

import java.util.List;

import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.marc.KeywordsStrategy;
import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.irt.eresources.marc.RecordCollectionFactory;
import edu.stanford.irt.eresources.marc.sul.InclusionStrategy;
import edu.stanford.irt.eresources.marc.sul.SulMARCRecordEresourceProcessor;
import edu.stanford.irt.eresources.pmc.PmcDedupAugmentation;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.RecordCollection;
import edu.stanford.lane.lcsh.LcshMapManager;

public class SfxRecordEresourceProcessor extends SulMARCRecordEresourceProcessor {

    private EresourceHandler eresourceHandler;

    private KeywordsStrategy keywordsStrategy;

    private LcshMapManager lcshMapManager = new LcshMapManager();

    private RecordCollectionFactory recordCollectionFactory;

    public SfxRecordEresourceProcessor(final EresourceHandler eresourceHandler, final KeywordsStrategy keywordsStrategy,
            final RecordCollectionFactory recordCollectionFactory, final LaneDedupAugmentation laneDedupAugmentation,
            final PmcDedupAugmentation pmcDedupAugmentation, final List<InclusionStrategy> inclusionStrategies) {
        super(eresourceHandler, keywordsStrategy, recordCollectionFactory, laneDedupAugmentation, pmcDedupAugmentation,
                inclusionStrategies);
        this.recordCollectionFactory = recordCollectionFactory;
        this.eresourceHandler = eresourceHandler;
        this.keywordsStrategy = keywordsStrategy;
    }

    @Override
    public void process() {
        RecordCollection rc = this.recordCollectionFactory.newRecordCollection(getStartTime());
        while (rc.hasNext()) {
            Record mr = rc.next();
            if (null != mr && hasIsbn(mr) && !isDuplicate(mr)) {
                this.eresourceHandler
                        .handleEresource(new SfxMarcEresource(mr, this.keywordsStrategy, this.lcshMapManager));
            }
        }
    }

    private boolean hasIsbn(final Record marcRecord) {
        return MARCRecordSupport.getFields(marcRecord, "020").count() > 0;
    }
}
