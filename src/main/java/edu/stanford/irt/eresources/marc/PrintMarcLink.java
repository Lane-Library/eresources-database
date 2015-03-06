package edu.stanford.irt.eresources.marc;

import org.marc4j.marc.DataField;

import edu.stanford.irt.eresources.Version;

public class PrintMarcLink extends MarcLink {

    public PrintMarcLink(final DataField dataField, final Version version) {
        super(dataField, version);
    }

    @Override
    public String getInstruction() {
        return null;
    }
}
