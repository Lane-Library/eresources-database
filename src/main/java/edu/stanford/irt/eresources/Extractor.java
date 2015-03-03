package edu.stanford.irt.eresources;

public interface Extractor<T> {
    
//    void extract(Transformer<T> transformer, Loader loader);
    
    boolean hasNext();
    
    T next();
}
