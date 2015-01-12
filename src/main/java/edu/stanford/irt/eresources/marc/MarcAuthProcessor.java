package edu.stanford.irt.eresources.marc;

import java.util.Date;

import org.marc4j.marc.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;

public class MarcAuthProcessor extends AbstractMarcProcessor {

    private EresourceHandler handler;

    private EresourceMarcReader marcReader;
    
    public MarcAuthProcessor(EresourceHandler handler, EresourceMarcReader marcReader, KeywordsStrategy keywordsStrategy) {
        super(keywordsStrategy);
        this.handler = handler;
        this.marcReader = marcReader;
    }

    @Override
    public void process() {
        Logger log = LoggerFactory.getLogger(getClass());
        log.info("enter process();");
        this.marcReader.setStartDate(new Date(getStartTime()));
        while (this.marcReader.hasNext()) {
            Record record = this.marcReader.next();
            Eresource eresource = new AuthMarcEresource(record, getKeywords(record).replaceAll("\\s\\s+", " ").trim());
            this.handler.handleEresource(eresource);
        }
        log.info("return process();");
    }
}
