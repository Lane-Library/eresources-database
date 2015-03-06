package edu.stanford.irt.eresources.marc;

import java.util.List;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import com.ibm.icu.text.Normalizer;

public class KeywordsStrategy {

    private static final String AGUMENTABLE_TAGS = "100|600|650|700";

    private static final String KEYWORD_TAGS = "020|022|030|035|901|902|903|907|941|942|943";

    private AuthTextAugmentation authTextAugmentation;

    public KeywordsStrategy(final AuthTextAugmentation authTextAugmentation) {
        this.authTextAugmentation = authTextAugmentation;
    }

    public String getKeywords(final Record record) {
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
            if (isAugmentable(tag, code)) {
                String authText = this.authTextAugmentation.getAuthAugmentations(subfield.getData());
                if (authText != null) {
                    sb.append(' ').append(authText);
                }
            }
        }
    }

    private void getKeywordsFromSubfield(final String tag, final char code, final String data, final StringBuilder sb) {
        String value = Normalizer.compose(data, false);
        if (sb.length() != 0) {
            sb.append(' ');
        }
        sb.append(value);
    }

    private boolean isAugmentable(final String tag, final char code) {
        return code == '0' && AGUMENTABLE_TAGS.indexOf(tag) != -1;
    }

    private boolean isKeywordSubfield(final String tag, final char code) {
        return !"907".equals(tag) || "xy".indexOf(code) > -1;
    }

    private boolean isKeywordTag(final String tag) {
        int tagNumber = Integer.parseInt(tag);
        return (tagNumber >= 100 && tagNumber < 900) || KEYWORD_TAGS.indexOf(tag) != -1;
    }
}
