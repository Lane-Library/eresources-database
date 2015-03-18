package edu.stanford.irt.eresources;

import java.util.List;

public interface Loader<L extends Eresource> {

    void load(List<L> eresources);
}
