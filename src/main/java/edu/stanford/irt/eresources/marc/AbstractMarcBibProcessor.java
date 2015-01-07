package edu.stanford.irt.eresources.marc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.EresourceInputStream;
import edu.stanford.irt.eresources.ItemCount;

public abstract class AbstractMarcBibProcessor extends AbstractMarcProcessor {

    public AbstractMarcBibProcessor(KeywordsStrategy keywordsStrategy) {
        super(keywordsStrategy);
    }

    private static final String HOLDINGS_CHARS = "uvxy";

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private EresourceHandler handler;

    private EresourceInputStream inputStream;

    private ItemCount itemCount;

    @Override
    public void process() {
        this.inputStream.setStartDate(new Timestamp(getStartTime()));
        MarcReader reader = new MarcStreamReader(this.inputStream);
        Record bib = null;
        List<Record> holdings = null;
        String keywords = null;
        while (reader.hasNext()) {
            Record record = reader.next();
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
            this.handler.handleEresource(new BibMarcEresource(bib, holdings, keywords, items));
            if (bib.getVariableField("249") != null) {
                this.handler.handleEresource(new AltTitleMarcEresource(bib, holdings, keywords, items));
            }
        }
    }

    public void setEresourceHandler(final EresourceHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("null handler");
        }
        this.handler = handler;
    }

    public void setInputStream(final EresourceInputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("null inputStream");
        }
        this.inputStream = inputStream;
    }

    public void setItemCount(final ItemCount itemCount) {
        this.itemCount = itemCount;
    }

    protected abstract Eresource createAltTitleEresource(Record bib, List<Record> holdings, String keywords, int[] items);

    protected abstract Eresource createEresource(Record bib, List<Record> holdings, String keywords, int[] items);

    private boolean isBib(final Record record) {
        return HOLDINGS_CHARS.indexOf(record.getLeader().getTypeOfRecord()) == -1;
    }
}
