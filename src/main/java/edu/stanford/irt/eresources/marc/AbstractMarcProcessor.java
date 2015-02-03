package edu.stanford.irt.eresources.marc;

import java.sql.Timestamp;

import org.marc4j.MarcReader;
import org.marc4j.marc.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceInputStream;

public abstract class AbstractMarcProcessor extends AbstractEresourceProcessor {

    private EresourceInputStream eresourceInputStream;

    private KeywordsStrategy keywordsStrategy;

    private MarcReaderFactory marcReaderFactory;

    public AbstractMarcProcessor(final EresourceInputStream eresourceInputStream,
            final MarcReaderFactory marcReaderFactory, final KeywordsStrategy keywordsStrategy) {
        this.eresourceInputStream = eresourceInputStream;
        this.marcReaderFactory = marcReaderFactory;
        this.keywordsStrategy = keywordsStrategy;
    }

    @Override
    public final void process() {
        Logger log = LoggerFactory.getLogger(getClass());
        log.info("enter process();");
        this.eresourceInputStream.setStartDate(new Timestamp(getStartTime()));
        doProcess(this.marcReaderFactory.newMarcReader(this.eresourceInputStream));
        log.info("return process();");
    }

    protected abstract void doProcess(MarcReader newMarcReader);

    protected String getKeywords(final Record record) {
        return this.keywordsStrategy.getKeywords(record);
    }
}
