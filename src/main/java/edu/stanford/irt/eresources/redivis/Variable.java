package edu.stanford.irt.eresources.redivis;

import java.util.Map;

public class Variable {

    private String description;

    private String label;

    private String name;

    private String url;

    private Map<String, String> valueLabels;

    public Variable() {
        // empty constructor
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @return the valueLabels
     */
    public Map<String, String> getValueLabels() {
        return this.valueLabels;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @param label
     *            the label to set
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * @param valueLabels
     *            the valueLabels to set
     */
    public void setValueLabels(final Map<String, String> valueLabels) {
        this.valueLabels = valueLabels;
    }
}
