package edu.stanford.irt.eresources.marc;

import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;

public abstract class AbstractMarcProcessor extends AbstractEresourceProcessor {

    private KeywordsStrategy keywordsStrategy;

    public AbstractMarcProcessor(final KeywordsStrategy keywordsStrategy) {
        this.keywordsStrategy = keywordsStrategy;
    }

    protected String getKeywords(final Record record) {
        return this.keywordsStrategy.getKeywords(record);
    }
}
