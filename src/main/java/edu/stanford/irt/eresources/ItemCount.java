package edu.stanford.irt.eresources;

import java.util.Map;

public class ItemCount {

    private Map<Integer, Integer> availables;

    private Map<Integer, Integer> totals;

    public ItemCount(final ItemService itemService) {
        this.availables = itemService.getAvailables();
        this.totals = itemService.getTotals();
    }

    public int[] itemCount(final int bibId) {
        int[] itemCount = new int[2];
        itemCount[0] = getCount(bibId, this.totals);
        if (itemCount[0] > 0) {
            itemCount[1] = getCount(bibId, this.availables);
        }
        return itemCount;
    }

    private int getCount(final int bibId, final Map<Integer, Integer> map) {
        Integer count = map.get(Integer.valueOf(bibId));
        return count != null ? count.intValue() : 0;
    }
}