package edu.stanford.irt.eresources.marc;

import java.util.stream.Stream;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;

public class MARCRecordSupport {
    
    protected static Stream<Field> getFieldStream(Record record, String tagString) {
        return record.getFields()
                .stream()
                .filter(f -> tagString.indexOf(f.getTag()) > -1);
    }
}
