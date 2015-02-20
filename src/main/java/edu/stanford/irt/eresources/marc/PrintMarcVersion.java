package edu.stanford.irt.eresources.marc;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;


public class PrintMarcVersion extends MarcVersion {

    public PrintMarcVersion(Record record) {
        super(record);
    }

    @Override
    protected Link createLink(DataField field, Version version) {
        return new PrintMarcLink(field, version);
    }
}