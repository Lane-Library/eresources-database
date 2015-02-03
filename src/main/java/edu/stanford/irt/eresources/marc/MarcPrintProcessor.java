package edu.stanford.irt.eresources.marc;

import java.util.List;

import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.EresourceInputStream;
import edu.stanford.irt.eresources.ItemCount;

public class MarcPrintProcessor extends AbstractMarcBibProcessor {

    public MarcPrintProcessor(final EresourceInputStream input, final EresourceHandler handler,
            final MarcReaderFactory marcReaderFactory, final ItemCount itemCount,
            final KeywordsStrategy keywordsStrategy) {
        super(input, handler, marcReaderFactory, itemCount, keywordsStrategy);
    }

    @Override
    protected Eresource createAltTitleEresource(final Record bib, final List<Record> holdings, final String keywords,
            final int[] items) {
        return new AltTitlePrintMarcEresource(bib, holdings, keywords, items);
    }

    @Override
    protected Eresource createEresource(final Record bib, final List<Record> holdings, final String keywords,
            final int[] items) {
        return new PrintMarcEresource(bib, holdings, keywords, items);
    }
}
