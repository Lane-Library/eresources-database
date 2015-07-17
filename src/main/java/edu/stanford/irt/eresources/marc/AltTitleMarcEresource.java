package edu.stanford.irt.eresources.marc;

import java.util.List;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import com.ibm.icu.text.Normalizer;

import edu.stanford.irt.eresources.ItemCount;

// TODO: create a TitleStrategy class
public class AltTitleMarcEresource extends BibMarcEresource {

    private Record record;

    public AltTitleMarcEresource(final List<Record> recordList, final String keywords, final ItemCount itemCount) {
        super(recordList, keywords, itemCount);
        this.record = recordList.get(0);
    }

    @Override
    public String getTitle() {
        StringBuilder title = new StringBuilder();
        DataField field249 = (DataField) this.record.getVariableField("249");
        for (Subfield subfield : field249.getSubfields()) {
            if ("anpq".indexOf(subfield.getCode()) > -1) {
                append(title, Normalizer.compose(subfield.getData(), false));
            }
        }
        return title.toString();
    }

    @Override
    public boolean isClone() {
        return true;
    }
}
