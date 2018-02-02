package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.ItemCount;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.RecordCollection;

public class MARCRecordEresourceProcessor extends AbstractEresourceProcessor {

    private static final String HOLDINGS_CHARS = "uvxy";

    private EresourceHandler eresourceHandler;

    private ItemCount itemCount;

    private KeywordsStrategy keywordsStrategy;

    private Record lastRecord;

    private List<Record> nextList;

    private RecordCollection recordCollection;

    private RecordCollectionFactory recordCollectionFactory;

    private TypeFactory typeFactory;

    public MARCRecordEresourceProcessor(final EresourceHandler eresourceHandler, final ItemCount itemCount,
            final KeywordsStrategy keywordsStrategy, final RecordCollectionFactory recordCollectionFactory,
            final TypeFactory typeFactory) {
        this.eresourceHandler = eresourceHandler;
        this.itemCount = itemCount;
        this.keywordsStrategy = keywordsStrategy;
        this.recordCollectionFactory = recordCollectionFactory;
        this.typeFactory = typeFactory;
    }

    private static boolean isHolding(final Record record) {
        return HOLDINGS_CHARS.indexOf(record.getLeaderByte(6)) > -1;
    }

    @Override
    public void process() {
        this.recordCollection = this.recordCollectionFactory.newRecordCollection(getStartTime());
        while (hasNext()) {
            List<Record> recordList = next();
            Record record = recordList.get(0);
            if (record.getLeaderByte(6) == 'q') {
                this.eresourceHandler
                        .handleEresource(new AuthMarcEresource(record, this.keywordsStrategy, this.typeFactory));
            } else {
                this.eresourceHandler.handleEresource(
                        new BibMarcEresource(recordList, this.keywordsStrategy, this.itemCount, this.typeFactory));
                int altTitleCount = (int) record.getFields().stream()
                        .filter((final Field f) -> "249".equals(f.getTag())).count();
                for (int i = 0; i < altTitleCount; i++) {
                    this.eresourceHandler.handleEresource(new AltTitleMarcEresource(recordList, this.keywordsStrategy,
                            this.typeFactory, this.itemCount, i + 1));
                }
            }
        }
    }

    private List<Record> getNextList() {
        List<Record> recordList = null;
        if (this.lastRecord == null && this.recordCollection.hasNext()) {
            this.lastRecord = this.recordCollection.next();
        }
        if (this.lastRecord != null) {
            recordList = new ArrayList<>();
            recordList.add(this.lastRecord);
            this.lastRecord = null;
            while (this.recordCollection.hasNext()) {
                Record next = this.recordCollection.next();
                if (isHolding(next)) {
                    recordList.add(next);
                } else {
                    this.lastRecord = next;
                    break;
                }
            }
        }
        return recordList;
    }

    private boolean hasNext() {
        if (this.nextList == null) {
            this.nextList = getNextList();
        }
        return this.nextList != null;
    }

    private List<Record> next() {
        if (hasNext()) {
            List<Record> next = this.nextList;
            this.nextList = null;
            return next;
        } else {
            throw new NoSuchElementException("no next next recordList");
        }
    }
}
