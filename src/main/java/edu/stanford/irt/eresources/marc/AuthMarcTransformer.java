package edu.stanford.irt.eresources.marc;

import java.util.Collections;
import java.util.List;

import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;

public class AuthMarcTransformer extends AbstractMarcTransformer<Record> {

    public AuthMarcTransformer(final KeywordsStrategy keywordsStrategy) {
        super(keywordsStrategy);
    }

    @Override
    public List<Eresource> transform(Record record) {
        return Collections.singletonList(new AuthMarcEresource(record, getKeywords(record).replaceAll("\\s\\s+", " ").trim()));
    }
}
