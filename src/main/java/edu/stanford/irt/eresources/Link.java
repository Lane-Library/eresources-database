package edu.stanford.irt.eresources;

public interface Link {

    String getAdditionalText();

    String getInstruction();

    String getLabel();

    String getLinkText();

    String getUrl();

    void setVersion(Version version);
}