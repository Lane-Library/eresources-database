package edu.stanford.irt.eresources.marc;

import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.AuthAugmentationInputStream;


public class AugmentationMarcReader implements MarcReader {
    
    private MarcReader reader;
    
    private DataSource dataSource;
    
    private Executor executor;
    
    public AugmentationMarcReader(DataSource dataSource, Executor executor) {
        this.dataSource = dataSource;
        this.executor = executor;
    }
    
    public void reset(String term, String tag) {
        this.reader = new MarcStreamReader(new AuthAugmentationInputStream(term, tag, dataSource, executor));
    }

    public boolean hasNext() {
        return reader.hasNext();
    }

    public Record next() {
        return reader.next();
    }
}
