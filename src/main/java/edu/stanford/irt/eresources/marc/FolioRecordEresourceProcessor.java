package edu.stanford.irt.eresources.marc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.lane.catalog.FolioRecord;
import edu.stanford.lane.catalog.FolioRecordCollection;
import edu.stanford.lane.catalog.Record.Field;

public class FolioRecordEresourceProcessor extends AbstractEresourceProcessor {

    private static final Logger log = LoggerFactory.getLogger(FolioRecordEresourceProcessor.class);

    private EresourceHandler eresourceHandler;

    private KeywordsStrategy keywordsStrategy;

    private HTTPLaneLocationsService locationsService;

    private RecordCollectionFactory recordCollectionFactory;

    public FolioRecordEresourceProcessor(final EresourceHandler eresourceHandler,
            final KeywordsStrategy keywordsStrategy, final RecordCollectionFactory recordCollectionFactory,
            final HTTPLaneLocationsService locationsService) {
        this.eresourceHandler = eresourceHandler;
        this.keywordsStrategy = keywordsStrategy;
        this.recordCollectionFactory = recordCollectionFactory;
        this.locationsService = locationsService;
    }

    @Override
    public void process() {
        FolioRecordCollection frc = this.recordCollectionFactory.newFolioRecordCollection(getStartTime());
        while (frc.hasNext()) {
            FolioRecord fr = frc.next();
            if (null != fr.getInstanceMarc()) {
                processMarcInstanceFolioHoldings(fr);
            } else if (null != fr.getInstance()) {
                processFolioInstanceFolioHoldings(fr);
            } else {
                log.info("skipping FolioRecord lacking both source MARC and instance data {}", fr);
            }
        }
    }

    private void processFolioInstanceFolioHoldings(final FolioRecord folioRecord) {
        this.eresourceHandler.handleEresource(new BibFolioEresource(folioRecord, this.locationsService));
    }

    private void processMarcInstanceFolioHoldings(final FolioRecord folioRecord) {
        this.eresourceHandler.handleEresource(
                new LaneMarcEresourceFolioHoldings(folioRecord, this.keywordsStrategy, this.locationsService));
        int altTitleCount = (int) folioRecord.getInstanceMarc().getFields().stream()
                .filter((final Field f) -> "249".equals(f.getTag())).count();
        for (int i = 0; i < altTitleCount; i++) {
            this.eresourceHandler.handleEresource(new AltTitleLaneMarcEresourceFolioHoldings(folioRecord,
                    this.keywordsStrategy, i + 1, this.locationsService));
        }
    }
}
