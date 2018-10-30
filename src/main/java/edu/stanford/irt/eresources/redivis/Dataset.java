package edu.stanford.irt.eresources.redivis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Dataset {

    private String accessLevel;

    private List<DatasetCollection> collections = Collections.emptyList();

    private String description;

    private List<Documentation> documentations = Collections.emptyList();

    private Entity entity;

    private String id;

    private String name;

    private Organization organization;

    private List<Tag> tags = Collections.emptyList();

    private TemporalRange temporalRange;

    private Date updatedAt;

    private String url;

    private List<Variable> variables = Collections.emptyList();

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
     * @return the collections
     */
    public List<DatasetCollection> getCollections() {
        return new ArrayList<>(this.collections);
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return the documentations
     */
    public List<Documentation> getDocumentations() {
        return new ArrayList<>(this.documentations);
    }

    /**
     * @return the entity
     */
    public Entity getEntity() {
        return this.entity;
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
     * @return the organization
     */
    public Organization getOrganization() {
        return this.organization;
    }

    /**
     * @return the tags
     */
    public List<Tag> getTags() {
        return new ArrayList<>(this.tags);
    }

    /**
     * @return the temporalRange
     */
    public TemporalRange getTemporalRange() {
        return this.temporalRange;
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

    /**
     * @return the variables
     */
    public List<Variable> getVariables() {
        return new ArrayList<>(this.variables);
    }
}
