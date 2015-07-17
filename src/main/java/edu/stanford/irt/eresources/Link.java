package edu.stanford.irt.eresources;

public interface Link {

    String getAdditionalText();

    String getInstruction();

    String getLabel();

    String getLinkText();

    String getUrl();

    /**
     * @deprecated Links should be immutable
     * @param version
     *            a Version
     */
    @Deprecated
    void setVersion(Version version);
}
