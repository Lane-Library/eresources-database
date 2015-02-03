package edu.stanford.irt.eresources.marc;

import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.EresourceInputStream;

public class MarcAuthProcessor extends AbstractMarcProcessor {

    private EresourceHandler handler;

    public MarcAuthProcessor(final EresourceInputStream eresourceInputStream, final EresourceHandler handler,
            final MarcReaderFactory marcReaderFactory, final KeywordsStrategy keywordsStrategy) {
        super(eresourceInputStream, marcReaderFactory, keywordsStrategy);
        this.handler = handler;
    }

    @Override
    protected void doProcess(final MarcReader marcReader) {
        while (marcReader.hasNext()) {
            Record record = marcReader.next();
            Eresource eresource = new AuthMarcEresource(record, getKeywords(record).replaceAll("\\s\\s+", " ").trim());
            this.handler.handleEresource(eresource);
        }
    }
}
