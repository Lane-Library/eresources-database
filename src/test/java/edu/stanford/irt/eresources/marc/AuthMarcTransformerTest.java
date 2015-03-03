package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Loader;

public class AuthMarcTransformerTest {

    private KeywordsStrategy keywordsStrategy;

    private AuthMarcTransformer processor;
    
    private Loader loader;
    
    private Record record;

    @Before
    public void setUp() {
        this.keywordsStrategy = createMock(KeywordsStrategy.class);
        this.processor = new AuthMarcTransformer(this.keywordsStrategy);
        this.loader = createMock(Loader.class);
        this.record = createMock(Record.class);
    }

    @Test
    public void testDoProcess() {
        expect(this.keywordsStrategy.getKeywords(this.record)).andReturn("keywords");
        replay(this.loader, this.record, this.keywordsStrategy);
        this.processor.transform(this.record);
        verify(this.loader, this.record, this.keywordsStrategy);
    }
}
