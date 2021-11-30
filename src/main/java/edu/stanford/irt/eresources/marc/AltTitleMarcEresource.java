package edu.stanford.irt.eresources.marc;

import java.util.List;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.ItemCount;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class AltTitleMarcEresource extends BibMarcEresource {

    private Record altTitleRecord;

    private int item;

    public AltTitleMarcEresource(final List<Record> recordList, final KeywordsStrategy keywordsStrategy,
            final TypeFactory typeFactory, final ItemCount itemCountBibs, final ItemCount itemCountHoldings,
            final int item, final HTTPLaneLocationsService locationsService) {
        super(recordList, keywordsStrategy, itemCountBibs, itemCountHoldings, typeFactory, locationsService);
        this.altTitleRecord = recordList.get(0);
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
        Field field = getFields(this.altTitleRecord, "249").collect(Collectors.toList()).get(this.item - 1);
        return field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'a').map(Subfield::getData)
                .findFirst().orElse(null);
    }
}
