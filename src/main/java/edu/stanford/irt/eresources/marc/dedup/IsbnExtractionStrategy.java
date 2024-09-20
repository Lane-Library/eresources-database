package edu.stanford.irt.eresources.marc.dedup;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.ISBNValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.TextHelper;

public class IsbnExtractionStrategy implements KeyExtractionStrategy {

    private static final Logger log = LoggerFactory.getLogger(IsbnExtractionStrategy.class);

    @Override
    public Set<String> extractKeys(Record marcRecord) {
        Set<String> keys = new HashSet<>();
        for (String isbn : MARCRecordSupport.getSubfieldData(marcRecord, "020", "a").map(String::trim)
                .map(TextHelper::cleanIsxn).filter((final String s) -> !s.isEmpty()).collect(Collectors.toSet())) {
            keys.add(LaneDedupAugmentation.KEY_ISBN + LaneDedupAugmentation.SEPARATOR + isbn);
            if (isbn.length() == 10) {
                try {
                    keys.add(LaneDedupAugmentation.KEY_ISBN + LaneDedupAugmentation.SEPARATOR
                            + ISBNValidator.getInstance().convertToISBN13(isbn));
                } catch (IllegalArgumentException e) {
                    log.warn("invalid ISBN: {}", isbn);
                }
            }
        }
        return keys;
    }
}