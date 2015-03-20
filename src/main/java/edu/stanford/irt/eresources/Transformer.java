package edu.stanford.irt.eresources;

import java.util.List;

public interface Transformer<E> {

    List<Eresource> transform(E input);
}
