package edu.stanford.irt.eresources.marc;

import org.marc4j.MarcReader;
import org.marc4j.marc.Record;
import org.marc4j.marc.impl.RecordImpl;

import edu.stanford.irt.eresources.Extractor;


public class RecordExtractor implements Extractor<Record> {
    
    private MarcReader marcReader;

    public RecordExtractor(MarcReader marcReader) {
        this.marcReader = marcReader;
    }

    @Override
    public boolean hasNext() {
        return this.marcReader.hasNext();
    }

    @Override
    public Record next() {
        return this.marcReader.next();
    }
}
