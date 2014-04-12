package edu.stanford.irt.eresources.marc;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;


public class AugmentedMarcLink implements Link {
    
    private Link link;
    private String label;
    private String instruction;

    public AugmentedMarcLink(Link link, String label, String instruction) {
        this.link = link;
        this.label = label;
        this.instruction = instruction;
    }

    public String getInstruction() {
        return this.instruction;
    }

    public String getLabel() {
        return this.label;
    }

    public String getUrl() {
        return this.link.getUrl();
    }

    @Override
    public String getAdditionalText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLinkText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setVersion(Version version) {
        // TODO Auto-generated method stub
        
    }
}
