package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.marc.sul.SulTypeFactory;
import edu.stanford.lane.catalog.FolioRecord;
import edu.stanford.lane.catalog.FolioRecordCollection;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;

public class FolioRecordEresourceProcessor extends AbstractEresourceProcessor {

    private static final Logger log = LoggerFactory.getLogger(FolioRecordEresourceProcessor.class);

    private EresourceHandler eresourceHandler;

    private KeywordsStrategy keywordsStrategy;

    private HTTPLaneLocationsService locationsService;

    private RecordCollectionFactory recordCollectionFactory;

    private SulTypeFactory typeFactory;

    public FolioRecordEresourceProcessor(final EresourceHandler eresourceHandler,
            final KeywordsStrategy keywordsStrategy, final RecordCollectionFactory recordCollectionFactory,
            final SulTypeFactory typeFactory, final HTTPLaneLocationsService locationsService) {
        this.eresourceHandler = eresourceHandler;
        this.keywordsStrategy = keywordsStrategy;
        this.recordCollectionFactory = recordCollectionFactory;
        this.typeFactory = typeFactory;
        this.locationsService = locationsService;
    }

    @Override
    public void process() {
        FolioRecordCollection frc = this.recordCollectionFactory.newFolioRecordCollection(getStartTime());
        while (frc.hasNext()) {
            FolioRecord fr = frc.next();
            if (null != fr.getInstanceMarc()) {
                processMarcSource(fr);
            } else if (null != fr.getInstance()) {
                processFolioSource(fr);
            } else {
                log.info("skipping FolioRecord lacking both source MARC and instance data {}", fr);
            }
        }
    }

    private void processFolioSource(final FolioRecord folioRecord) {
        this.eresourceHandler.handleEresource(new BibFolioEresource(folioRecord, this.locationsService));
    }

    private void processMarcSource(final FolioRecord folioRecord) {
        List<Record> recordList = new ArrayList<>();
        Record bibRecord = folioRecord.getInstanceMarc();
        recordList.add(bibRecord);
        recordList.addAll(folioRecord.getHoldingsMarc());
        this.eresourceHandler.handleEresource(
                new BibMarcEresource(recordList, this.keywordsStrategy, this.typeFactory, this.locationsService));
        int altTitleCount = (int) bibRecord.getFields().stream().filter((final Field f) -> "249".equals(f.getTag()))
                .count();
        for (int i = 0; i < altTitleCount; i++) {
            this.eresourceHandler.handleEresource(new AltTitleMarcEresource(recordList, this.keywordsStrategy,
                    this.typeFactory, i + 1, this.locationsService));
        }
    }
}
