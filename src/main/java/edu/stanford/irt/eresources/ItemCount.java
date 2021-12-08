package edu.stanford.irt.eresources;

import java.util.Map;

public class ItemCount {

    private static final int MAP_SIZE = 2;

    private Map<Integer, Integer> availables;

    private Map<Integer, Integer> totals;

    public ItemCount(final Map<Integer, Integer> availables, final Map<Integer, Integer> totals) {
        this.availables = availables;
        this.totals = totals;
    }

    public int[] itemCount(final int recordId) {
        int[] itemCount = new int[MAP_SIZE];
        itemCount[0] = getCount(recordId, this.totals);
        if (itemCount[0] > 0) {
            itemCount[1] = getCount(recordId, this.availables);
        }
        return itemCount;
    }

    private int getCount(final int recordId, final Map<Integer, Integer> map) {
        Integer count = map.get(Integer.valueOf(recordId));
        return count != null ? count.intValue() : 0;
    }
}
