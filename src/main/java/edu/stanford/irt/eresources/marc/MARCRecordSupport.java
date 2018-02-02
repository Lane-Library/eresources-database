package edu.stanford.irt.eresources.marc;

import java.util.stream.Stream;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class MARCRecordSupport {

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
}
