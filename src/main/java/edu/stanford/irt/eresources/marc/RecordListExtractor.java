package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Extractor;

public class RecordListExtractor implements Extractor<List<Record>> {

    private static final String HOLDINGS_CHARS = "uvxy";

    boolean started = false;

    private Record lastRecord;

    private MarcReader marcReader;

    private List<Record> nextList;

    public RecordListExtractor(final MarcReader marcReader) {
        this.marcReader = marcReader;
    }

    @Override
    public boolean hasNext() {
        if (this.nextList == null) {
            this.nextList = getNextList();
        }
        return this.nextList != null;
    }

    @Override
    public List<Record> next() {
        if (hasNext()) {
            List<Record> next = this.nextList;
            this.nextList = null;
            return next;
        } else {
            throw new NoSuchElementException("no next next recordList");
        }
    }

    private List<Record> getNextList() {
        List<Record> recordList = null;
        if (this.lastRecord == null && this.marcReader.hasNext()) {
            this.lastRecord = this.marcReader.next();
        }
        if (this.lastRecord != null) {
            recordList = new ArrayList<Record>();
            recordList.add(this.lastRecord);
            this.lastRecord = null;
            while(this.marcReader.hasNext()) {
                Record next = this.marcReader.next();
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

    private boolean isHolding(final Record record) {
        return HOLDINGS_CHARS.indexOf(record.getLeader().getTypeOfRecord()) > -1;
    }
}
