package edu.stanford.irt.eresources.marc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import com.ibm.icu.text.Normalizer;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.EresourceInputStream;
import edu.stanford.irt.eresources.ItemCount;
import edu.stanford.irt.eresources.sax.AuthTextAugmentation;

public class MarcPrintProcessor extends AbstractEresourceProcessor {

    private static final String HOLDINGS_CHARS = "uvxy";

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private AuthTextAugmentation authTextAugmentation;

    private EresourceHandler handler;

    private EresourceInputStream inputStream;

    private ItemCount itemCount;

    @Override
    public void process() {
        this.inputStream.setStartDate(new Timestamp(getStartTime()));
        MarcReader reader = new MarcStreamReader(this.inputStream);
        Record bib = null;
        List<Record> holdings = null;
        String keywords = null;
        while (reader.hasNext()) {
            Record record = reader.next();
            if (isBib(record)) {
                if (bib != null) {
                    int[] items = this.itemCount.itemCount(bib.getControlNumber());
                    this.handler.handleEresource(new PrintMarcEresource(bib, holdings, keywords, items));
                    if (bib.getVariableField("249") != null) {
                        this.handler.handleEresource(new AltTitlePrintMarcEresource(bib, holdings, keywords, items));
                    }
                }
                bib = record;
                keywords = WHITESPACE.matcher(getKeywords(record)).replaceAll(" ");
                holdings = new ArrayList<Record>();
            } else {
                holdings.add(record);
            }
        }
        if (bib != null) {
            int[] items = this.itemCount.itemCount(bib.getControlNumber());
            this.handler.handleEresource(new PrintMarcEresource(bib, holdings, keywords, items));
            if (bib.getVariableField("249") != null) {
                this.handler.handleEresource(new AltTitlePrintMarcEresource(bib, holdings, keywords, items));
            }
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

    public void setItemCount(final ItemCount itemCount) {
        this.itemCount = itemCount;
    }

    private String getKeywords(final Record record) {
        StringBuilder sb = new StringBuilder();
        for (DataField field : record.getDataFields()) {
            int tagNumber = Integer.parseInt(field.getTag());
            if (((tagNumber >= 100) && (tagNumber < 900)) || (tagNumber == 20) || (tagNumber == 22)
                    || (tagNumber == 30) || (tagNumber == 35) || ((tagNumber >= 901) && (tagNumber <= 903))
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
        sb.append(' ');
        return sb.toString();
    }

    private boolean isBib(final Record record) {
        return HOLDINGS_CHARS.indexOf(record.getLeader().getTypeOfRecord()) == -1;
    }
}
