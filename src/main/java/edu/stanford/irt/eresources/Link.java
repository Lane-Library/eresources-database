package edu.stanford.irt.eresources;

public interface Link {

    String getAdditionalText();

    String getLabel();

    String getLinkText();

    String getUrl();

    default boolean isRelatedResourceLink() {
        return false;
    }

    default boolean isResourceLink() {
        return false;
    }

    void setVersion(Version version);
}
