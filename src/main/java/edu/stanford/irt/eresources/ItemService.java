package edu.stanford.irt.eresources;

import java.util.Map;

public interface ItemService {

    Map<Integer, Integer> getTotals();

    Map<Integer, Integer> getAvailables();
}
