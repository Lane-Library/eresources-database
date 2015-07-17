package edu.stanford.irt.eresources;

public class ItemCount {

    private int available;

    private int total;

    public ItemCount(final int total, final int available) {
        this.total = total;
        this.available = available;
    }

    public int getAvailable() {
        return this.available;
    }

    public int getTotal() {
        return this.total;
    }
}
