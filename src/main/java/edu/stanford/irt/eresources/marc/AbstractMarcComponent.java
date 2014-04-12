package edu.stanford.irt.eresources.marc;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

import com.ibm.icu.text.Normalizer;

public abstract class AbstractMarcComponent {

    protected void append(final StringBuilder sb, final String value) {
        if (value != null) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(value);
        }
    }

    protected String getSubfieldData(final DataField field, final char code) {
        String result = null;
        if (field != null) {
            Subfield subfield = field.getSubfield(code);
            if (subfield != null) {
                result = Normalizer.compose(subfield.getData(), false);
            }
        }
        return result;
    }
}
