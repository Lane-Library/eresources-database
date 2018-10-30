package edu.stanford.irt.eresources.redivis;

public class Documentation {

    private String requiredAccessLevel;

    private String text;

    public Documentation() {
        // empty constructor
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
