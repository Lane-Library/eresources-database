package edu.stanford.irt.eresources.redivis;

public class DatasetCollection {

    private String id;

    private String kind;

    private String name;

    private String url;

    public DatasetCollection() {
        // empty constructor
    }

    public DatasetCollection(final String id, final String kind, final String name, final String url) {
        this.id = id;
        this.kind = kind;
        this.name = name;
        this.url = url;
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
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
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }
}
