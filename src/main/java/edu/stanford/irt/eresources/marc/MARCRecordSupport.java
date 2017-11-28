package edu.stanford.irt.eresources.marc;

import java.util.stream.Stream;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class MARCRecordSupport {
    
    protected static Stream<Field> getFields(Record record, String tagString) {
        return record.getFields()
                .stream()
                .filter(f -> tagString.indexOf(f.getTag()) > -1);
    }
    
    protected static Stream<String> getSubfieldData(Record record, String tagString, String codeString) {
        return getFields(record, tagString)
                .flatMap(f -> f.getSubfields().stream())
                .filter(s -> codeString.indexOf(s.getCode()) > -1)
                .map(Subfield::getData);
    }
    
    protected static Stream<String> getSubfieldData(Record record, String tagString) {
        return getFields(record, tagString)
                .flatMap(f -> f.getSubfields().stream())
                .map(Subfield::getData);
    }
    
    protected static Stream<String> getSubfieldData(Stream<Field> fieldStream, String codeString) {
        return fieldStream.flatMap(f -> f.getSubfields().stream())
                .filter(s -> codeString.indexOf(s.getCode()) > -1)
                .map(Subfield::getData);
    }
}
