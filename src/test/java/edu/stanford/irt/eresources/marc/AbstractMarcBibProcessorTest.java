package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.EresourceInputStream;
import edu.stanford.irt.eresources.ItemCount;

public class AbstractMarcBibProcessorTest {

    private class TestAbstractMarcBibProcessor extends AbstractMarcBibProcessor {

        public TestAbstractMarcBibProcessor(final EresourceInputStream input, final EresourceHandler handler,
                final MarcReaderFactory marcReaderFactory, final ItemCount itemCount,
                final KeywordsStrategy keywordsStrategy) {
            super(input, handler, marcReaderFactory, itemCount, keywordsStrategy);
        }

        @Override
        protected Eresource createAltTitleEresource(final Record bib, final List<Record> holdings,
                final String keywords, final int[] items) {
            return null;
        }

        @Override
        protected Eresource createEresource(final Record bib, final List<Record> holdings, final String keywords,
                final int[] items) {
            return null;
        }
    }

    private EresourceHandler eresourceHandler;

    private VariableField field;

    private ItemCount itemCount;

    private KeywordsStrategy keywordStrategy;

    private Leader leader;

    private MarcReader marcReader;

    private AbstractMarcBibProcessor processor;

    private Record record;

    @Before
    public void setUp() {
        this.eresourceHandler = createMock(EresourceHandler.class);
        this.itemCount = createMock(ItemCount.class);
        this.keywordStrategy = createMock(KeywordsStrategy.class);
        this.processor = new TestAbstractMarcBibProcessor(null, this.eresourceHandler, null, this.itemCount,
                this.keywordStrategy);
        this.marcReader = createMock(MarcReader.class);
        this.record = createMock(Record.class);
        this.leader = createMock(Leader.class);
        this.field = createMock(VariableField.class);
    }

    @Test
    public void testDoProcess() {
        expect(this.marcReader.hasNext()).andReturn(true).times(2);
        expect(this.marcReader.next()).andReturn(this.record).times(2);
        expect(this.record.getLeader()).andReturn(this.leader).times(2);
        expect(this.leader.getTypeOfRecord()).andReturn('a');
        expect(this.keywordStrategy.getKeywords(this.record)).andReturn("keywords");
        expect(this.leader.getTypeOfRecord()).andReturn('u');
        expect(this.marcReader.hasNext()).andReturn(true).times(2);
        expect(this.record.getControlNumber()).andReturn("12");
        expect(this.itemCount.itemCount("12")).andReturn(new int[] { 1, 1 });
        this.eresourceHandler.handleEresource(null);
        expect(this.record.getVariableField("249")).andReturn(this.field);
        this.eresourceHandler.handleEresource(null);
        expect(this.marcReader.next()).andReturn(this.record).times(2);
        expect(this.record.getLeader()).andReturn(this.leader).times(2);
        expect(this.leader.getTypeOfRecord()).andReturn('a');
        expect(this.keywordStrategy.getKeywords(this.record)).andReturn("keywords");
        expect(this.leader.getTypeOfRecord()).andReturn('u');
        expect(this.marcReader.hasNext()).andReturn(false);
        expect(this.record.getControlNumber()).andReturn("14");
        expect(this.itemCount.itemCount("14")).andReturn(new int[] { 1, 1 });
        this.eresourceHandler.handleEresource(null);
        expect(this.record.getVariableField("249")).andReturn(this.field);
        this.eresourceHandler.handleEresource(null);
        replay(this.eresourceHandler, this.marcReader, this.itemCount, this.keywordStrategy, this.record, this.leader,
                this.field);
        this.processor.doProcess(this.marcReader);
        verify(this.eresourceHandler, this.marcReader, this.itemCount, this.keywordStrategy, this.record, this.leader,
                this.field);
    }
}
