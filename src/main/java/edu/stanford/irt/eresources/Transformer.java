package edu.stanford.irt.eresources;

import java.util.List;

public interface Transformer<T> {

    List<Eresource> transform(T input);
}
