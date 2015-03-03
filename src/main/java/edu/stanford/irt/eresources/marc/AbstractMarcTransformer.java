package edu.stanford.irt.eresources.marc;

import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Transformer;

public abstract class AbstractMarcTransformer<T> implements Transformer<T> {

    private KeywordsStrategy keywordsStrategy;

    public AbstractMarcTransformer(final KeywordsStrategy keywordsStrategy) {
        this.keywordsStrategy = keywordsStrategy;
    }

   protected String getKeywords(final Record record) {
        return this.keywordsStrategy.getKeywords(record);
    }
}
