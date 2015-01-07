package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.ItemCount;

public class AbstractMarcBibProcessorTest {

    private class TestAbstractMarcBibProcessor extends AbstractMarcBibProcessor {

        public TestAbstractMarcBibProcessor(EresourceHandler handler, MarcReader marcReader, ItemCount itemCount, final KeywordsStrategy keywordsStrategy) {
            super(handler, marcReader, itemCount, keywordsStrategy);
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

    private KeywordsStrategy keywordStrategy;

    private AbstractMarcBibProcessor processor;

    private ItemCount itemCount;

    private MarcReader marcReader;

    private EresourceHandler eresourceHandler;

    private Record record;

    private Leader leader;

    private VariableField field;

    @Before
    public void setUp() {
        this.eresourceHandler = createMock(EresourceHandler.class);
        this.marcReader = createMock(MarcReader.class);
        this.itemCount = createMock(ItemCount.class);
        this.keywordStrategy = createMock(KeywordsStrategy.class);
        this.processor = new TestAbstractMarcBibProcessor(this.eresourceHandler, this.marcReader, this.itemCount, this.keywordStrategy);
        this.record = createMock(Record.class);
        this.leader = createMock(Leader.class);
        this.field  =createMock(VariableField.class);
    }

    @Test
    public void testProcess() {
        expect(this.marcReader.hasNext()).andReturn(true).times(2);
        expect(this.marcReader.next()).andReturn(this.record).times(2);
        expect(this.record.getLeader()).andReturn(this.leader).times(2);
        expect(this.leader.getTypeOfRecord()).andReturn('a');
        expect(this.keywordStrategy.getKeywords(this.record)).andReturn("keywords");
        expect(this.leader.getTypeOfRecord()).andReturn('u');
        expect(this.marcReader.hasNext()).andReturn(true).times(2);
        expect(this.record.getControlNumber()).andReturn("12");
        expect(this.itemCount.itemCount("12")).andReturn(new int[] {1,1});
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
        expect(this.itemCount.itemCount("14")).andReturn(new int[] {1,1});
        this.eresourceHandler.handleEresource(null);
        expect(this.record.getVariableField("249")).andReturn(this.field);
        this.eresourceHandler.handleEresource(null);
        replay(this.eresourceHandler, this.marcReader, this.itemCount, this.keywordStrategy, this.record, this.leader, this.field);
        this.processor.process();
        verify(this.eresourceHandler, this.marcReader, this.itemCount, this.keywordStrategy, this.record, this.leader, this.field);
    }
}
