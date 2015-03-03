package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Extractor;


public class RecordListExtractor implements Extractor<List<Record>> {

    private static final String HOLDINGS_CHARS = "uvxy";
    
    private MarcReader marcReader;
    private Record lastRecord;
    private List<Record> nextList;
    boolean started = false;
    
    public RecordListExtractor(MarcReader marcReader) {
        this.marcReader = marcReader;
    }


    private boolean isBib(final Record record) {
        return HOLDINGS_CHARS.indexOf(record.getLeader().getTypeOfRecord()) == -1;
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
    
    private List<Record> getNextList() {
        List<Record> recordList = null;
        if (this.marcReader.hasNext()) {
            Record record = this.marcReader.next();
            if (!this.started) {
                this.lastRecord = record;
            }
            if (this.lastRecord != null) {
                recordList = new ArrayList<Record>();
                recordList.add(this.lastRecord);
            }
            while(this.marcReader.hasNext() && !isBib(record = this.marcReader.next())) {
                recordList.add(record);
            }
            this.lastRecord = isBib(record) ? record : null;
        } else if (this.lastRecord != null) {
            return Collections.singletonList(this.lastRecord);
        }
        return recordList;
    }


    @Override
    public List<Record> next() {
        List<Record> next = this.nextList;
        this.nextList = getNextList();
        return next;
    }
}
