package edu.stanford.irt.eresources.marc;

import java.util.List;

import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;

public class PrintMarcTransformer extends AbstractBibMarcTransformer {

    public PrintMarcTransformer(final ItemCount itemCount, final KeywordsStrategy keywordsStrategy) {
        super(itemCount, keywordsStrategy);
    }

    @Override
    protected Eresource createAltTitleEresource(final List<Record> recordList, final String keywords, final int[] items) {
        return new AltTitlePrintMarcEresource(recordList, keywords, items);
    }

    @Override
    protected Eresource createEresource(final List<Record> recordList, final String keywords, final int[] items) {
        return new PrintMarcEresource(recordList, keywords, items);
    }
}
