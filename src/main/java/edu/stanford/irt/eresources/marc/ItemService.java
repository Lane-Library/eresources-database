package edu.stanford.irt.eresources.marc;

import edu.stanford.irt.eresources.ItemCount;

public interface ItemService {

    ItemCount getItemCount(String controlNumber);
}
