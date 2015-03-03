package edu.stanford.irt.eresources.marc;

import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;

public class AuthMarcTransformer extends AbstractMarcTransformer<Record> {

    public AuthMarcTransformer(final KeywordsStrategy keywordsStrategy) {
        super(keywordsStrategy);
    }

    @Override
    public Eresource[] transform(Record record) {
        return new Eresource[] {
                new AuthMarcEresource(record, getKeywords(record).replaceAll("\\s\\s+", " ").trim())
        };
    }
}
