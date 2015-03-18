package edu.stanford.irt.eresources;

import java.util.List;

public interface Transformer<E, L extends Eresource> {

    List<L> transform(E input);
}
