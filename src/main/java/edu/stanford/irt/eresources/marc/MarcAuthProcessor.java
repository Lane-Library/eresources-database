package edu.stanford.irt.eresources.marc;

import java.sql.Timestamp;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import com.ibm.icu.text.Normalizer;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.EresourceInputStream;
import edu.stanford.irt.eresources.sax.AuthTextAugmentation;

public class MarcAuthProcessor extends AbstractEresourceProcessor {

    private AuthTextAugmentation authTextAugmentation;

    private EresourceHandler handler;

    private EresourceInputStream inputStream;

    public void process() {
        this.inputStream.setStartDate(new Timestamp(getStartTime()));
        MarcReader reader = new MarcStreamReader(this.inputStream);
        while (reader.hasNext()) {
            Record record = reader.next();
            Eresource eresource = new AuthMarcEresource(record, getKeywords(record));
            this.handler.handleEresource(eresource);
        }
    }

    public void setAuthTextAugmentation(final AuthTextAugmentation authTextAugmentation) {
        if (authTextAugmentation == null) {
            throw new IllegalArgumentException("null authTextAugmentation");
        }
        this.authTextAugmentation = authTextAugmentation;
    }

    public void setEresourceHandler(final EresourceHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("null handler");
        }
        this.handler = handler;
    }

    public void setInputStream(final EresourceInputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("null inputStream");
        }
        this.inputStream = inputStream;
    }

    private String getKeywords(final Record record) {
        StringBuilder sb = new StringBuilder();
        for (DataField field : record.getDataFields()) {
            int tagNumber = Integer.parseInt(field.getTag());
            if (((tagNumber >= 100) && (tagNumber < 900)) || (tagNumber == 20) || (tagNumber == 22) || (tagNumber == 30)
                    || (tagNumber == 35) || ((tagNumber >= 901) && (tagNumber <= 903))
                    || ((tagNumber >= 941) && (tagNumber <= 943)) || tagNumber == 907) {
                for (Subfield subfield : field.getSubfields()) {
                    if (tagNumber != 907 || "xy".indexOf(subfield.getCode()) > -1) {
                        String value = Normalizer.compose(subfield.getData(), false);
                        if (sb.length() != 0) {
                            sb.append(' ');
                        }
                        sb.append(value);
                        if ((tagNumber == 100 || tagNumber == 600 || tagNumber == 650 || tagNumber == 700)
                                && subfield.getCode() == 'a') {
                            String authText = this.authTextAugmentation.getAuthAugmentations(value, field.getTag());
                            if (authText != null) {
                                sb.append(' ').append(authText);
                            }
                        }
                    }
                }
            }
        }
        return sb.toString();
    }
}
