package edu.stanford.irt.eresources.sax;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

public class SAXLink implements Link {

    private String instruction;

    private String label;

    private String url;

    private Version version;

    @Override
    public String getAdditionalText() {
        StringBuilder sb = new StringBuilder();
        if (this.instruction != null) {
            sb.append(" ").append(this.instruction);
        }
        if (this.version.getPublisher() != null) {
            sb.append(" ").append(this.version.getPublisher());
        }
        return sb.toString();
    }

    @Override
    public String getInstruction() {
        return this.instruction;
    }

    @Override
    public String getLabel() {
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

    @Override
    public String getUrl() {
        return this.url;
    }

    public void setInstruction(final String instruction) {
        this.instruction = instruction;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public void setVersion(final Version version) {
        this.version = version;
    }
}
