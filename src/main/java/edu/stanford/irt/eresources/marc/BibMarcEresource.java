package edu.stanford.irt.eresources.marc;

import java.util.List;

import edu.stanford.irt.eresources.ItemService;
import edu.stanford.lane.catalog.Record;

/**
 * An Eresource that encapsulates the marc Records from which it is derived.
 */
public class BibMarcEresource extends AbstractMarcEresource {

    public BibMarcEresource(final List<Record> recordList, final KeywordsStrategy keywordsStrategy,
            final ItemService itemService, final TypeFactory typeFactory,
            final HTTPLaneLocationsService locationsService) {
        this.record = recordList.get(0);
        this.holdings = recordList.subList(1, recordList.size());
        this.keywordsStrategy = keywordsStrategy;
        this.itemService = itemService;
        this.typeFactory = typeFactory;
        this.locationsService = locationsService;
    }
}
