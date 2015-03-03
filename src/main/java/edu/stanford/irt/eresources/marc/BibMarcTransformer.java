package edu.stanford.irt.eresources.marc;

import java.util.List;

import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;

public class BibMarcTransformer extends AbstractBibMarcTransformer {

    public BibMarcTransformer(final ItemCount itemCount,
            final KeywordsStrategy keywordsStrategy) {
        super(itemCount, keywordsStrategy);
    }

    @Override
    protected Eresource createAltTitleEresource(List<Record> recordList, final String keywords,
            final int[] items) {
        return new AltTitleMarcEresource(recordList, keywords, items);
    }

    @Override
    protected Eresource createEresource(final List<Record> recordList, final String keywords,
            final int[] items) {
        return new BibMarcEresource(recordList, keywords, items);
    }
}
