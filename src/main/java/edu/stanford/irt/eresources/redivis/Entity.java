package edu.stanford.irt.eresources.redivis;

public class Entity {

    private String kind;

    private String name;

    public Entity() {
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
}
