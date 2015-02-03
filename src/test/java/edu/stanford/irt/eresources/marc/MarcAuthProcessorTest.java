package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.MarcReader;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;

public class MarcAuthProcessorTest {

    private EresourceHandler eresourceHandler;

    private KeywordsStrategy keywordsStrategy;

    private MarcReader marcReader;

    private MarcAuthProcessor processor;

    @Before
    public void setUp() {
        this.eresourceHandler = createMock(EresourceHandler.class);
        this.keywordsStrategy = createMock(KeywordsStrategy.class);
        this.processor = new MarcAuthProcessor(null, this.eresourceHandler, null, this.keywordsStrategy);
        this.marcReader = createMock(MarcReader.class);
    }

    @Test
    public void testDoProcess() {
        expect(this.marcReader.hasNext()).andReturn(true);
        expect(this.marcReader.next()).andReturn(null);
        expect(this.keywordsStrategy.getKeywords(null)).andReturn("keywords");
        this.eresourceHandler.handleEresource(isA(Eresource.class));
        expect(this.marcReader.hasNext()).andReturn(false);
        replay(this.eresourceHandler, this.marcReader, this.keywordsStrategy);
        this.processor.doProcess(this.marcReader);
        verify(this.eresourceHandler, this.marcReader, this.keywordsStrategy);
    }
}
