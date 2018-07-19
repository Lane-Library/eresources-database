package edu.stanford.irt.eresources.marc;

import java.util.stream.Stream;

import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class MARCRecordSupport {

    private static final int F008_07 = 7;

    private static final int F008_11 = 11;

    private static final int F008_15 = 15;

    protected static Stream<Field> getFields(final Record record, final String tagString) {
        return record.getFields().stream().filter((final Field f) -> tagString.indexOf(f.getTag()) > -1);
    }

    protected static Stream<String> getSubfieldData(final Record record, final String tagString) {
        return getFields(record, tagString).flatMap((final Field f) -> f.getSubfields().stream())
                .map(Subfield::getData);
    }

    protected static Stream<String> getSubfieldData(final Record record, final String tagString,
            final String codeString) {
        return getFields(record, tagString).flatMap((final Field f) -> f.getSubfields().stream())
                .filter((final Subfield s) -> codeString.indexOf(s.getCode()) > -1).map(Subfield::getData);
    }

    protected static Stream<String> getSubfieldData(final Stream<Field> fieldStream, final String codeString) {
        return fieldStream.flatMap((final Field f) -> f.getSubfields().stream())
                .filter((final Subfield s) -> codeString.indexOf(s.getCode()) > -1).map(Subfield::getData);
    }

    protected static int getYear(final Record record) {
        int year = 0;
        String dateField = getFields(record, "008").map(Field::getData).findFirst().orElse("0000000000000000");
        String endDate = TextParserHelper.parseYear(dateField.substring(F008_11, F008_15));
        if (endDate != null) {
            year = Integer.parseInt(endDate);
        } else {
            String beginDate = TextParserHelper.parseYear(dateField.substring(F008_07, F008_11));
            if (beginDate != null) {
                year = Integer.parseInt(beginDate);
            }
        }
        return year;
    }
}
