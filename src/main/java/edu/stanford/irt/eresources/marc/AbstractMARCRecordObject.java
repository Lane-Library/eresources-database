package edu.stanford.irt.eresources.marc;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public abstract class AbstractMARCRecordObject {
    
    protected static String getFirstSubfieldData(Record record, String tag, char code) {
        return record.getFields()
                .stream()
                .filter(f -> f.getTag().equals(tag))
                .map(f -> getFirstSubfieldData(f, code))
                .findFirst()
                .orElse(null);
    }
    
    protected static String getFirstSubfieldData(Field field, char code) {
        return field.getSubfields()
                .stream()
                .filter(s -> s.getCode() == code)
                .map(Subfield::getData)
                .findFirst()
                .orElse(null);
    }

    protected static String getSubfieldData(final Field field, final char code) {
        String result = null;
        if (field != null) {
            Subfield subfield = field.getSubfields()
                    .stream()
                    .filter(s -> s.getCode() == code)
                    .findFirst()
                    .orElse(null);
            if (subfield != null) {
                result = subfield.getData();
            }
        }
        return result;
    }
}
