package edu.stanford.irt.eresources;

public interface Extractor<T> {

    boolean hasNext();

    T next();
}
