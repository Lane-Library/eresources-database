package edu.stanford.irt.eresources.marc;

import java.util.List;

import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;

public class MarcBibProcessor extends AbstractMarcBibProcessor {

    @Override
    protected Eresource createAltTitleEresource(final Record bib, final List<Record> holdings, final String keywords,
            final int[] items) {
        return new AltTitleMarcEresource(bib, holdings, keywords, items);
    }

    @Override
    protected Eresource createEresource(final Record bib, final List<Record> holdings, final String keywords,
            final int[] items) {
        return new BibMarcEresource(bib, holdings, keywords, items);
    }
}
