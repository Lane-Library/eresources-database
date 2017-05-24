package edu.stanford.irt.eresources;

import java.util.Map;

public class ItemCount {

    private Map<Integer, Integer> availables;

    private Map<Integer, Integer> totals;

    private ItemService itemService;

    public ItemCount(final ItemService itemService) {
        this.itemService = itemService;
    }

    public int[] itemCount(final int bibId) {
        int[] itemCount = new int[2];
        itemCount[0] = getCount(bibId, getTotals());
        if (itemCount[0] > 0) {
            itemCount[1] = getCount(bibId, getAvailables());
        }
        return itemCount;
    }

    private Map<Integer, Integer> getAvailables() {
        if (this.availables == null) {
            this.availables = this.itemService.getAvailables();
        }
        return this.availables;
    }

    private Map<Integer, Integer> getTotals() {
        if (this.totals == null) {
            this.totals = this.itemService.getTotals();
        }
        return this.totals;
    }

    private int getCount(final int bibId, final Map<Integer, Integer> map) {
        Integer count = map.get(Integer.valueOf(bibId));
        return count != null ? count.intValue() : 0;
    }
}