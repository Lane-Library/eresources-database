package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LanguageMapTest {

    LanguageMap map;

    @Test
    public final void testLanguageMap() {
        this.map = new LanguageMap();
        assertEquals("English", this.map.getLanguage("eng"));
        assertEquals(null, this.map.getLanguage("foo"));
    }
}
