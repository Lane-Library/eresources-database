package edu.stanford.irt.eresources.redivis;

public class Documentation {

    private String kind;

    private String name;

    private String requiredAccessLevel;

    private String text;

    public Documentation() {
        // empty constructor
    }

    /**
     * @return the kind
     */
    public String getKind() {
        return this.kind;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the requiredAccessLevel
     */
    public String getRequiredAccessLevel() {
        return this.requiredAccessLevel;
    }

    /**
     * @return the text
     */
    public String getText() {
        return this.text;
    }

    /**
     * @param kind
     *            the kind to set
     */
    public void setKind(final String kind) {
        this.kind = kind;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param requiredAccessLevel
     *            the requiredAccessLevel to set
     */
    public void setRequiredAccessLevel(final String requiredAccessLevel) {
        this.requiredAccessLevel = requiredAccessLevel;
    }

    /**
     * @param text
     *            the text to set
     */
    public void setText(final String text) {
        this.text = text;
    }
}
