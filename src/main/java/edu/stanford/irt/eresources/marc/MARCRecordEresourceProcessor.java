package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.lane.catalog.FolioRecord;
import edu.stanford.lane.catalog.FolioRecordCollection;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;

public class MARCRecordEresourceProcessor extends AbstractEresourceProcessor {

    private EresourceHandler eresourceHandler;

    private KeywordsStrategy keywordsStrategy;

    private HTTPLaneLocationsService locationsService;

    private RecordCollectionFactory recordCollectionFactory;

    private SulTypeFactory typeFactory;

    public MARCRecordEresourceProcessor(final EresourceHandler eresourceHandler,
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
            Record bibRecord = fr.getInstanceMarc();
            // skip if there's no MARC
            if (null == bibRecord) {
                continue;
            }
            List<Record> recordList = new ArrayList<>();
            recordList.add(bibRecord);
            recordList.addAll(fr.getHoldingsMarc());
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
}
