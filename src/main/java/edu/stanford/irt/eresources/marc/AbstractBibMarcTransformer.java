package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;

public abstract class AbstractBibMarcTransformer extends AbstractMarcTransformer<List<Record>> {

    @Override
    public List<Eresource> transform(List<Record> recordList) {
        List<Eresource> eresources = new ArrayList<Eresource>();
        Record bib = recordList.get(0);
        String keywords = WHITESPACE.matcher(getKeywords(bib)).replaceAll(" ");
        int[] items = this.itemCount.itemCount(bib.getControlNumber());
        eresources.add(createEresource(recordList, keywords, items));
        if (bib.getVariableField("249") != null) {
            eresources.add(createAltTitleEresource(recordList, keywords, items));
        }
        return eresources;
    }

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private ItemCount itemCount;

    public AbstractBibMarcTransformer(final ItemCount itemCount, final KeywordsStrategy keywordsStrategy) {
        super(keywordsStrategy);
        this.itemCount = itemCount;
    }

    protected abstract Eresource createAltTitleEresource(List<Record> recordList, String keywords, int[] items);

    protected abstract Eresource createEresource(List<Record> recordList, String keywords, int[] items);
}
