package edu.stanford.irt.eresources.redivis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Dataset {

    private String accessLevel;

    private String description;

    private String id;

    private String name;

    private List<Tag> tags = Collections.emptyList();

    private Date updatedAt;

    private String url;

    public Dataset() {
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
     * @return the tags
     */
    public List<Tag> getTags() {
        return new ArrayList<>(this.tags);
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
