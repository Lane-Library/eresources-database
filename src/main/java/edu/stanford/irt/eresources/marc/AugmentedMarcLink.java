package edu.stanford.irt.eresources.marc;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

public class AugmentedMarcLink implements Link {

    private String instruction;

    private String label;

    private Link link;

    public AugmentedMarcLink(final Link link, final String label, final String instruction) {
        this.link = link;
        this.label = label;
        this.instruction = instruction;
    }

    @Override
    public String getAdditionalText() {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getUrl() {
        return this.link.getUrl();
    }

    @Override
    public void setVersion(final Version version) {
        // TODO Auto-generated method stub
    }
}
