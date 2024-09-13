package edu.stanford.irt.eresources.marc;

import edu.stanford.lane.catalog.FolioRecord;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class AltTitleLaneMarcEresourceFolioHoldings extends LaneMarcEresourceFolioHoldings {

    private Record altTitleRecord;

    private int item;

    public AltTitleLaneMarcEresourceFolioHoldings(final FolioRecord folioRecord,
            final KeywordsStrategy keywordsStrategy, final int item, final HTTPLaneLocationsService locationsService) {
        super(folioRecord, keywordsStrategy, locationsService);
        this.altTitleRecord = marcRecord;
        this.item = item;
    }

    @Override
    public String getId() {
        return super.getId() + "-clone-" + this.item;
    }

    @Override
    public String getSortTitle() {
        return getTitle();
    }

    @Override
    public String getTitle() {
        Field field = getFields(this.altTitleRecord, "249").toList().get(this.item - 1);
        return field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'a').map(Subfield::getData)
                .findFirst().orElse(null);
    }
}
