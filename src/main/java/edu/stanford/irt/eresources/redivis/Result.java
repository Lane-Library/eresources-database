package edu.stanford.irt.eresources.redivis;

import java.util.Date;

public class Result {

    private String accessLevel;

    private String description;

    private String name;

    private String referenceId;

    private Date updatedAt;

    private String url;

    public Result() {
        // empty constructor
    }

    /**
     * @return the accessLevel
     */
    public String getAccessLevel() {
        return this.accessLevel;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the id
     */
    public String getReferenceId() {
        return this.referenceId;
    }

    /**
     * @return the updatedAt
     */
    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }
}
