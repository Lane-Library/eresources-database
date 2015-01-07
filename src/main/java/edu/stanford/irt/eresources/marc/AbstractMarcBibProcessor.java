package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.ItemCount;

public abstract class AbstractMarcBibProcessor extends AbstractMarcProcessor {

    private static final String HOLDINGS_CHARS = "uvxy";

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private EresourceHandler handler;

    private ItemCount itemCount;

    private MarcReader marcReader;

    public AbstractMarcBibProcessor(final EresourceHandler handler, final MarcReader marcReader,
            final ItemCount itemCount, final KeywordsStrategy keywordsStrategy) {
        super(keywordsStrategy);
        this.handler = handler;
        this.marcReader = marcReader;
        this.itemCount = itemCount;
    }

    @Override
    public void process() {
        Record bib = null;
        List<Record> holdings = null;
        String keywords = null;
        while (this.marcReader.hasNext()) {
            Record record = this.marcReader.next();
            if (isBib(record)) {
                if (bib != null) {
                    int[] items = this.itemCount.itemCount(bib.getControlNumber());
                    this.handler.handleEresource(createEresource(bib, holdings, keywords, items));
                    if (bib.getVariableField("249") != null) {
                        this.handler.handleEresource(createAltTitleEresource(bib, holdings, keywords, items));
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
            this.handler.handleEresource(createEresource(bib, holdings, keywords, items));
            if (bib.getVariableField("249") != null) {
                this.handler.handleEresource(createAltTitleEresource(bib, holdings, keywords, items));
            }
        }
    }

    protected abstract Eresource createAltTitleEresource(Record bib, List<Record> holdings, String keywords, int[] items);

    protected abstract Eresource createEresource(Record bib, List<Record> holdings, String keywords, int[] items);

    private boolean isBib(final Record record) {
        return HOLDINGS_CHARS.indexOf(record.getLeader().getTypeOfRecord()) == -1;
    }
}
