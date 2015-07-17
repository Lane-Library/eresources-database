package edu.stanford.irt.eresources.marc;

import java.util.List;

import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.ItemCount;

public class BibMarcTransformer extends AbstractBibMarcTransformer {

    public BibMarcTransformer(final ItemCounter itemCounter, final KeywordsStrategy keywordsStrategy) {
        super(itemCounter, keywordsStrategy);
    }

    @Override
    protected Eresource createAltTitleEresource(final List<Record> recordList, final String keywords,
            final ItemCount itemCount) {
        return new AltTitleMarcEresource(recordList, keywords, itemCount);
    }

    @Override
    protected Eresource createEresource(final List<Record> recordList, final String keywords, final ItemCount itemCount) {
        return new BibMarcEresource(recordList, keywords, itemCount);
    }
}
