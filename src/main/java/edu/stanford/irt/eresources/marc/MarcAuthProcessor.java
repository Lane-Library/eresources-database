package edu.stanford.irt.eresources.marc;

import java.sql.Timestamp;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.EresourceInputStream;

public class MarcAuthProcessor extends AbstractMarcProcessor {

    private EresourceHandler handler;

    private EresourceInputStream inputStream;

    @Override
    public void process() {
        this.inputStream.setStartDate(new Timestamp(getStartTime()));
        MarcReader reader = new MarcStreamReader(this.inputStream);
        while (reader.hasNext()) {
            Record record = reader.next();
            Eresource eresource = new AuthMarcEresource(record, getKeywords(record).replaceAll("\\s\\s+", " ").trim());
            this.handler.handleEresource(eresource);
        }
    }

    public void setEresourceHandler(final EresourceHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("null handler");
        }
        this.handler = handler;
    }

    public void setInputStream(final EresourceInputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("null inputStream");
        }
        this.inputStream = inputStream;
    }
}
