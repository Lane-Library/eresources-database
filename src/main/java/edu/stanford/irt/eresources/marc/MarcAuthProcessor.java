package edu.stanford.irt.eresources.marc;

import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;

public class MarcAuthProcessor extends AbstractMarcProcessor {

    private EresourceHandler handler;

    private MarcReader marcReader;
    
    public MarcAuthProcessor(EresourceHandler handler, MarcReader marcReader, KeywordsStrategy keywordsStrategy) {
        super(keywordsStrategy);
        this.handler = handler;
        this.marcReader = marcReader;
    }

    @Override
    public void process() {
        while (this.marcReader.hasNext()) {
            Record record = this.marcReader.next();
            Eresource eresource = new AuthMarcEresource(record, getKeywords(record).replaceAll("\\s\\s+", " ").trim());
            this.handler.handleEresource(eresource);
        }
    }
}
