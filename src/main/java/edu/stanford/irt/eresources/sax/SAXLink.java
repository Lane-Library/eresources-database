package edu.stanford.irt.eresources.sax;

import edu.stanford.irt.eresources.AbstractLink;
import edu.stanford.irt.eresources.Version;

public class SAXLink extends AbstractLink {

    private String additionalText;

    private String instruction;

    private String label;

    private String linkText;

    private String url;

    private Version version;

    @Override
    public String getAdditionalText() {
        if (this.additionalText == null) {
            this.additionalText = getAdditionalText(this.instruction, this.version.getAdditionalText(), this.version.getPublisher());
        }
        return this.additionalText;
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
        if (this.linkText == null) {
            this.linkText = getLinkText(this.label, this.version);
        }
        return this.linkText;
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
