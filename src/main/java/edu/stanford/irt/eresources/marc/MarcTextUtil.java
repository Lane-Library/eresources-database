package edu.stanford.irt.eresources.marc;

import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public abstract class MarcTextUtil {

    private MarcTextUtil() {
        // utility class
    }

    protected static String getSubfieldData(final Field field, final char code) {
        String result = null;
        if (field != null) {
            Subfield subfield = field.getSubfields().stream().filter(s -> s.getCode() == code).findFirst().orElse(null);
            if (subfield != null) {
                result = subfield.getData();
            }
        }
        return result;
    }
}
