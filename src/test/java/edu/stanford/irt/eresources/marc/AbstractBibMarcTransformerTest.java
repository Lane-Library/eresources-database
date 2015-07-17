package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.ItemCount;
import edu.stanford.irt.eresources.Loader;

public class AbstractBibMarcTransformerTest {

    private class TestAbstractBibMarcTransformer extends AbstractBibMarcTransformer {

        public TestAbstractBibMarcTransformer(final ItemCounter itemCounter, final KeywordsStrategy keywordsStrategy) {
            super(itemCounter, keywordsStrategy);
        }

        @Override
        protected Eresource createAltTitleEresource(final List<Record> recordList, final String keywords,
                final ItemCount itemCount) {
            return null;
        }

        @Override
        protected Eresource createEresource(final List<Record> recordList, final String keywords,
                final ItemCount itemCount) {
            return null;
        }
    }

    private VariableField field;

    private ItemCounter itemCounter;

    private KeywordsStrategy keywordStrategy;

    private Loader loader;

    private AbstractBibMarcTransformer processor;

    private Record record;

    @Before
    public void setUp() {
        this.itemCounter = createMock(ItemCounter.class);
        this.keywordStrategy = createMock(KeywordsStrategy.class);
        this.processor = new TestAbstractBibMarcTransformer(this.itemCounter, this.keywordStrategy);
        this.record = createMock(Record.class);
        this.field = createMock(VariableField.class);
        this.loader = createMock(Loader.class);
    }

    @Test
    public void testDoProcess() {
        expect(this.keywordStrategy.getKeywords(this.record)).andReturn("keywords");
        expect(this.record.getControlNumber()).andReturn("12");
        expect(this.itemCounter.getItemCount("12")).andReturn(new ItemCount(1, 1));
        expect(this.record.getVariableField("249")).andReturn(this.field);
        replay(this.loader, this.itemCounter, this.keywordStrategy, this.record, this.field);
        this.processor.transform(Arrays.asList(new Record[] { this.record, this.record }));
        verify(this.loader, this.itemCounter, this.keywordStrategy, this.record, this.field);
    }
}
