package edu.stanford.irt.eresources.marc;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

import edu.stanford.irt.eresources.AbstractLink;
import edu.stanford.irt.eresources.Version;

/**
 * A Link that encapsulates the DataField from which it is derived.
 */
public class MarcLink extends AbstractLink {

    private String additionalText;

    private DataField dataField;

    private String instruction;

    private boolean instructionDone;

    private String label;

    private boolean labelDone;

    private String linkText;

    private String url;

    private boolean urlDone;

    private Version version;

    public MarcLink(final DataField dataField, final Version version) {
        this.dataField = dataField;
        this.version = version;
    }

    @Override
    public String getAdditionalText() {
        if (this.additionalText == null) {
            this.additionalText = getAdditionalText(getInstruction(), this.version.getPublisher());
        }
        return this.additionalText;
    }

    @Override
    public String getInstruction() {
        if (!this.instructionDone) {
            doInstruction();
        }
        return this.instruction;
    }

    @Override
    public String getLabel() {
        if (!this.labelDone) {
            doLabel();
        }
        return this.label;
    }

    @Override
    public String getLinkText() {
        if (this.linkText == null) {
            this.linkText = getLinkText(getLabel(), this.version);
        }
        return this.linkText;
    }

    @Override
    public String getUrl() {
        if (!this.urlDone) {
            doUrl();
        }
        return this.url;
    }

    @Override
    public void setVersion(final Version version) {
        // TODO Auto-generated method stub
    }

    private void doInstruction() {
        // TODO: review getting last ^i, that's what happens with SAX;
        for (Subfield subfield : this.dataField.getSubfields('i')) {
            this.instruction = subfield.getData();
        }
        this.instructionDone = true;
    }

    private void doLabel() {
        String l = MarcTextUtil.getSubfieldData(this.dataField, 'q');
        if (l == null) {
            l = MarcTextUtil.getSubfieldData(this.dataField, 'z');
        }
        if (l != null && (l.indexOf('(') == 0) && (l.indexOf(')') == l.length() - 1) && (l.length() > 2)) {
            l = l.substring(1, l.length() - 1);
        }
        this.label = l;
        this.labelDone = true;
    }

    private void doUrl() {
        this.url = MarcTextUtil.getSubfieldData(this.dataField, 'u');
        this.urlDone = true;
    }
}