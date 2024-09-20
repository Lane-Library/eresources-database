package edu.stanford.irt.eresources.marc.dedup;

import java.util.Set;

import edu.stanford.lane.catalog.Record;

public interface KeyExtractionStrategy {

    Set<String> extractKeys(Record marcRecord);
}
