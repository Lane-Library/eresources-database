package edu.stanford.irt.eresources.redivis;

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
        return this.collections;
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
        return this.documentations;
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
        return this.tags;
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
        return this.variables;
    }

    /**
     * @param accessLevel
     *            the accessLevel to set
     */
    public void setAccessLevel(final String accessLevel) {
        this.accessLevel = accessLevel;
    }

    /**
     * @param collections
     *            the collections to set
     */
    public void setCollections(final List<DatasetCollection> collections) {
        this.collections = collections;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @param documentations
     *            the documentations to set
     */
    public void setDocumentations(final List<Documentation> documentations) {
        this.documentations = documentations;
    }

    /**
     * @param entity
     *            the entity to set
     */
    public void setEntity(final Entity entity) {
        this.entity = entity;
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
     * @param organization
     *            the organization to set
     */
    public void setOrganization(final Organization organization) {
        this.organization = organization;
    }

    /**
     * @param tags
     *            the tags to set
     */
    public void setTags(final List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * @param temporalRange
     *            the temporalRange to set
     */
    public void setTemporalRange(final TemporalRange temporalRange) {
        this.temporalRange = temporalRange;
    }

    /**
     * @param updatedAt
     *            the updatedAt to set
     */
    public void setUpdatedAt(final Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * @param variables
     *            the variables to set
     */
    public void setVariables(final List<Variable> variables) {
        this.variables = variables;
    }
}
