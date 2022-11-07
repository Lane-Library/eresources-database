package edu.stanford.irt.eresources.marc.sul;

import java.util.regex.Pattern;

import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;

public interface InclusionStrategy {

    static final Pattern FICTION = Pattern.compile("(^|\\b)(?<!non\\-)fiction(\\S|\\b)", Pattern.CASE_INSENSITIVE);

    boolean isAcceptable(final Record marcRecord);

    default boolean isFiction(final Record marcRecord) {
        return MARCRecordSupport.getSubfieldData(marcRecord, "650", "av")
                .anyMatch((final String s) -> FICTION.matcher(s).find())
                || MARCRecordSupport.getSubfieldData(marcRecord, "651", "av")
                        .anyMatch((final String s) -> FICTION.matcher(s).find())
                || MARCRecordSupport.getSubfieldData(marcRecord, "655", "av")
                        .anyMatch((final String s) -> FICTION.matcher(s).find());
    }
}
