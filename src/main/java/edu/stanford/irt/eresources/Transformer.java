package edu.stanford.irt.eresources;

public interface Transformer<T> {
    
    Eresource[] transform(T input);
}
