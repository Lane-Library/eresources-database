package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.ItemCount;

public abstract class AbstractBibMarcTransformer extends AbstractMarcTransformer<List<Record>> {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private ItemCounter itemCounter;

    public AbstractBibMarcTransformer(final ItemCounter itemCounter, final KeywordsStrategy keywordsStrategy) {
        super(keywordsStrategy);
        this.itemCounter = itemCounter;
    }

    @Override
    public List<Eresource> transform(final List<Record> recordList) {
        List<Eresource> eresources = new ArrayList<Eresource>();
        Record bib = recordList.get(0);
        String keywords = WHITESPACE.matcher(getKeywords(bib)).replaceAll(" ");
        ItemCount itemCount = this.itemCounter.getItemCount(bib.getControlNumber());
        eresources.add(createEresource(recordList, keywords, itemCount));
        if (bib.getVariableField("249") != null) {
            eresources.add(createAltTitleEresource(recordList, keywords, itemCount));
        }
        return eresources;
    }

    protected abstract Eresource createAltTitleEresource(List<Record> recordList, String keywords, ItemCount itemCount);

    protected abstract Eresource createEresource(List<Record> recordList, String keywords, ItemCount itemCount);
}
