package edu.stanford.irt.eresources.marc;

import java.util.Collection;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

/**
 * A Link that encapsulates the DataField from which it is derived.
 */
public class MarcLink extends AbstractMarcComponent implements Link {

    private DataField dataField;

    private String instruction;

    private boolean instructionDone;

    private String label;

    private boolean labelDone;

    private String url;

    private boolean urlDone;

    private MarcVersion version;

    public MarcLink(final DataField dataField, final MarcVersion marcVersion) {
        this.dataField = dataField;
        this.version = marcVersion;
    }

    @Override
    public String getAdditionalText() {
        StringBuilder sb = new StringBuilder();
        String inst = getInstruction();
        if (inst != null) {
            sb.append(" ").append(inst);
        }
        if (this.version.getPublisher() != null) {
            sb.append(" ").append(this.version.getPublisher());
        }
        return sb.toString();
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
        StringBuilder sb = new StringBuilder();
        if ("impact factor".equalsIgnoreCase(this.label)) {
            sb.append("Impact Factor");
        } else {
            String summaryHoldings = this.version.getSummaryHoldings();
            if (summaryHoldings != null && this.version.getLinks().size() == 1) {
                sb.append(summaryHoldings);
                String dates = this.version.getDates();
                if (dates != null && dates.length() > 0) {
                    sb.append(", ").append(dates);
                }
            } else {
                if (this.label != null) {
                    sb.append(this.label);
                }
            }
            if (sb.length() == 0) {
                sb.append(this.label);
            }
            String description = this.version.getDescription();
            if (description != null && description.length() > 0) {
                sb.append(" ").append(description);
            }
        }
        return sb.toString();
    }

    public Collection<String> getSubsets() {
        return this.version.getSubsets();
    }

    @Override
    public String getUrl() {
        if (!this.urlDone) {
            doUrl();
        }
        return this.url;
    }

    public boolean isNoProxy() {
        return this.version.isProxy();
    }

    @Override
    public void setVersion(final Version version) {
        // TODO Auto-generated method stub
    }

    private void doInstruction() {
        //TODO: review getting last ^i, that's what happens with SAX;
        for (Subfield subfield : this.dataField.getSubfields('i')) {
            this.instruction = subfield.getData();
        }
        this.instructionDone = true;
    }

    private void doLabel() {
        String l = getSubfieldData(this.dataField, 'q');
        if (l == null) {
            l = getSubfieldData(this.dataField, 'z');
        }
        if (l != null && (l.indexOf('(') == 0) && (l.indexOf(')') == l.length() - 1) && (l.length() > 2)) {
            l = l.substring(1, l.length() - 1);
        }
        this.label = l;
        this.labelDone = true;
    }

    private void doUrl() {
        this.url = getSubfieldData(this.dataField, 'u');
        this.urlDone = true;
    }
}