package edu.stanford.irt.eresources.marc.sul;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.irt.eresources.marc.type.TypeFactory;
import edu.stanford.lane.catalog.Record;

public class AcceptableKeywordStrategy implements InclusionStrategy {

    List<String> acceptableKeywords;

    List<String> acceptablePrimaryTypes;

    public AcceptableKeywordStrategy(final List<String> acceptableKeywords, final List<String> acceptablePrimaryTypes) {
        this.acceptableKeywords = acceptableKeywords;
        this.acceptablePrimaryTypes = acceptablePrimaryTypes;
    }

    // check keywords for record inclusion when
    // - record is not fiction
    // - record doesn't have LC or NLM call numbers
    // - record has appropriate type
    @Override
    public boolean isAcceptable(final Record marcRecord) {
        return isAcceptablePrimaryType(marcRecord) && !isFiction(marcRecord)
                && !MARCRecordSupport.hasNLMCallNumber(marcRecord)
                && MARCRecordSupport.extractLCCallNumbers(marcRecord).isEmpty() && hasAcceptableKeywords(marcRecord);
    }

    private boolean hasAcceptableKeywords(final Record marcRecord) {
        String marc = marcRecord.toString().toLowerCase(Locale.US);
        List<String> marcWords = Arrays.asList(marc.split("\\s"));
        for (String keyword : this.acceptableKeywords) {
            if (marcWords.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAcceptablePrimaryType(final Record marcRecord) {
        return this.acceptablePrimaryTypes.contains(TypeFactory.getPrimaryType(marcRecord));
    }
}
