package edu.stanford.irt.eresources.marc;

import java.sql.Timestamp;
import java.util.Date;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.EresourceInputStream;
import edu.stanford.irt.eresources.StartDateAware;


public class EresourceMarcReader implements MarcReader, StartDateAware {
    
    private EresourceInputStream input;
    
    private MarcReader marcReader;

    public EresourceMarcReader(EresourceInputStream input) {
        this.input = input;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.input.setStartDate(new Timestamp(startDate.getTime()));
    }

    @Override
    public boolean hasNext() {
        if (this.marcReader == null) {
            this.marcReader = new MarcStreamReader(this.input);
        }
        return this.marcReader.hasNext();
    }

    @Override
    public Record next() {
        return (this.marcReader.next());
    }
}
