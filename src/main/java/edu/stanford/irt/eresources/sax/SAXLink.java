package edu.stanford.irt.eresources.sax;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

public class SAXLink implements Link {

    private String instruction = null;

    private String label = null;

    private String url = null;

    private Version version = null;

    @Override
    public String getAdditionalText() {
        // this once appended publisher, versionText and instruction data
        // only instruction data is required now because publisher and versionText are displayed separately
        return this.instruction;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getLinkText() {
        StringBuilder sb = new StringBuilder();
        String holdingsAndDates = this.version.getHoldingsAndDates();
        if (holdingsAndDates != null && this.version.getLinks().size() == 1) {
            sb.append(holdingsAndDates);
        } else {
            if (this.label != null) {
                sb.append(this.label);
            }
        }
        if (sb.length() == 0) {
            sb.append(this.label);
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

    public void setVersion(final Version version) {
        this.version = version;
    }
}
