package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        boolean hasNext = false;
        if (this.nextList != null) {
            hasNext = true;
        } else if (!this.started) {
            hasNext = (this.nextList = getNextList()) != null;
            this.started = true;
        }
        return hasNext;
    }

    @Override
    public List<Record> next() {
        List<Record> next = this.nextList;
        this.nextList = getNextList();
        return next;
    }

    private List<Record> getNextList() {
        List<Record> recordList = null;
        if (this.marcReader.hasNext()) {
            Record record = this.marcReader.next();
            recordList = new ArrayList<Record>();
            if (!this.started) {
                recordList.add(record);
            } else {
                recordList.add(this.lastRecord);
                if (isHolding(record)) {
                    recordList.add(record);
                }
            }
            while (this.marcReader.hasNext() && isHolding(record = this.marcReader.next())) {
                recordList.add(record);
            }
            this.lastRecord = isHolding(record) ? null : record;
        } else if (this.lastRecord != null) {
            return Collections.singletonList(this.lastRecord);
        }
        return recordList;
    }

    private boolean isHolding(final Record record) {
        return HOLDINGS_CHARS.indexOf(record.getLeader().getTypeOfRecord()) > -1;
    }
}
