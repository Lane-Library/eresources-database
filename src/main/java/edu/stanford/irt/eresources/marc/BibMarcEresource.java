package edu.stanford.irt.eresources.marc;

import java.util.List;

import edu.stanford.lane.catalog.Record;

/**
 * An Eresource that encapsulates the marc Records from which it is derived.
 */
public class BibMarcEresource extends AbstractMarcEresource {

    public BibMarcEresource(final List<Record> recordList, final KeywordsStrategy keywordsStrategy,
            final HTTPLaneLocationsService locationsService) {
        this.marcRecord = recordList.get(0);
        this.holdings = recordList.subList(1, recordList.size());
        this.keywordsStrategy = keywordsStrategy;
        this.locationsService = locationsService;
    }
}
