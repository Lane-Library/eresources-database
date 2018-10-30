package edu.stanford.irt.eresources.redivis;

public class Organization {

    private String id;

    private String name;

    private String shortName;

    public Organization() {
        // empty constructor
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param shortName
     *            the shortName to set
     */
    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }
}
