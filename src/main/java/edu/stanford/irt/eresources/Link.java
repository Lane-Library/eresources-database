package edu.stanford.irt.eresources;

public interface Link {

    String getAdditionalText();

    String getLabel();

    String getLinkText();

    String getUrl();

    void setVersion(Version version);
}
