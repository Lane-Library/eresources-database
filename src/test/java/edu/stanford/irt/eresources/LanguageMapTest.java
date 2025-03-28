package edu.stanford.irt.eresources;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LanguageMapTest {

    LanguageMap map;

    @Test
    final void testLanguageMap() {
        this.map = new LanguageMap();
        assertEquals("English", this.map.getLanguage("eng"));
        assertEquals(null, this.map.getLanguage("foo"));
    }
}
