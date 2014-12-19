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

public class MarcBibProcessor extends AbstractEresourceProcessor {

    private static final String AGUMENTABLE_TAGS = "100|600|650|700";

    private static final String HOLDINGS_CHARS = "uvxy";

    private static final String KEYWORD_TAGS = "020|022|030|035|901|902|903|907|941|942|943";

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
                    this.handler.handleEresource(new BibMarcMarcEresource(bib, holdings, keywords, items));
                    if (bib.getVariableField("249") != null) {
                        this.handler.handleEresource(new AltTitleMarcEresource(bib, holdings, keywords, items));
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
            this.handler.handleEresource(new BibMarcMarcEresource(bib, holdings, keywords, items));
            if (bib.getVariableField("249") != null) {
                this.handler.handleEresource(new AltTitleMarcEresource(bib, holdings, keywords, items));
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
            String tag = field.getTag();
            if (isKeywordTag(tag)) {
                getKeywordsFromField(tag, field.getSubfields(), sb);
            }
        }
        sb.append(' ');
        return sb.toString();
    }

    private void getKeywordsFromField(final String tag, final List<Subfield> subfields, final StringBuilder sb) {
        for (Subfield subfield : subfields) {
            char code = subfield.getCode();
            if (isKeywordSubfield(tag, code)) {
                getKeywordsFromSubfield(tag, code, subfield.getData(), sb);
            }
        }
    }

    private void getKeywordsFromSubfield(final String tag, final char code, final String data, final StringBuilder sb) {
        String value = Normalizer.compose(data, false);
        if (sb.length() != 0) {
            sb.append(' ');
        }
        sb.append(value);
        if (isAugmentable(tag, code)) {
            String authText = this.authTextAugmentation.getAuthAugmentations(value, tag);
            if (authText != null) {
                sb.append(' ').append(authText);
            }
        }
    }

    private boolean isAugmentable(final String tag, final char code) {
        return code == 'a' && AGUMENTABLE_TAGS.indexOf(tag) != -1;
    }

    private boolean isBib(final Record record) {
        return HOLDINGS_CHARS.indexOf(record.getLeader().getTypeOfRecord()) == -1;
    }

    private boolean isKeywordSubfield(final String tag, final char code) {
        return !"907".equals(tag) || "xy".indexOf(code) > -1;
    }

    private boolean isKeywordTag(final String tag) {
        int tagNumber = Integer.parseInt(tag);
        return (tagNumber >= 100 && tagNumber < 900) || KEYWORD_TAGS.indexOf(tag) != -1;
    }
}
