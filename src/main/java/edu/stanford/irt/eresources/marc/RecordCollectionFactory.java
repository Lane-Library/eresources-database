package edu.stanford.irt.eresources.marc;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.lane.catalog.RecordCollection;

public class RecordCollectionFactory {

    private CatalogRecordService catalogRecordService;

    public RecordCollectionFactory(final CatalogRecordService catalogRecordService) {
        this.catalogRecordService = catalogRecordService;
    }

    public RecordCollection newRecordCollection(final long startTime) {
        return new RecordCollection(this.catalogRecordService.getRecordStream(startTime));
    }
}
