package edu.stanford.irt.eresources.sax;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.TextStrategy;
import edu.stanford.irt.eresources.Version;

public class SAXLink implements Link {

    private static final TextStrategy TEXT_STRATEGY = new TextStrategy();

    private String additionalText;

    private String instruction;

    private String label;

    private String linkText;

    private String url;

    private Version version;

    @Override
    public String getAdditionalText() {
        if (this.additionalText == null) {
            this.additionalText = TEXT_STRATEGY.getAdditionalText(this.instruction, this.version.getPublisher());
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
            this.linkText = TEXT_STRATEGY.getLinkText(this.label, this.version);
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
