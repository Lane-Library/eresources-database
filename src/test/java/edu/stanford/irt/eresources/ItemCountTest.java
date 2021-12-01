package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ItemCountTest {

    ItemCount itemCount;

    ItemService itemService;

    Map<Integer, Integer> map;

    @Before
    public void setUp() throws Exception {
        this.map = new HashMap<>();
        this.map.put(2, 2);
        this.itemCount = new ItemCount(this.map, this.map);
    }

    @Test
    public final void testItemCount() {
        assertEquals(0, this.itemCount.itemCount(1)[0]);
        assertEquals(0, this.itemCount.itemCount(1)[1]);
        assertEquals(2, this.itemCount.itemCount(2)[0]);
        assertEquals(2, this.itemCount.itemCount(2)[1]);
    }
}
