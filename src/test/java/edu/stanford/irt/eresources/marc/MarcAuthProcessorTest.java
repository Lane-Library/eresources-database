package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;

public class MarcAuthProcessorTest {

    private EresourceHandler eresourceHandler;

    private KeywordsStrategy keywordsStrategy;

    private MarcReader marcReader;

    private MarcAuthProcessor processor;

    private Record record;

    @Before
    public void setUp() {
        this.eresourceHandler = createMock(EresourceHandler.class);
        this.marcReader = createMock(MarcReader.class);
        this.keywordsStrategy = createMock(KeywordsStrategy.class);
        this.processor = new MarcAuthProcessor(this.eresourceHandler, this.marcReader, this.keywordsStrategy);
        this.record = createMock(Record.class);
    }

    @Test
    public void testProcess() {
        expect(this.marcReader.hasNext()).andReturn(true);
        expect(this.marcReader.next()).andReturn(this.record);
        expect(this.keywordsStrategy.getKeywords(this.record)).andReturn("keywords");
        this.eresourceHandler.handleEresource(isA(Eresource.class));
        expect(this.marcReader.hasNext()).andReturn(false);
        replay(this.eresourceHandler, this.marcReader, this.keywordsStrategy, this.record);
        this.processor.process();
        verify(this.eresourceHandler, this.marcReader, this.keywordsStrategy, this.record);
    }
}
