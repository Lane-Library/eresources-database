package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LanguageMapTest {

    LanguageMap map;

    File origPropFile;

    File renamedPropFile;

    @Before
    public void setUp() throws Exception {
        this.origPropFile = new File("languages.properties");
        this.renamedPropFile = new File("languages.properties.bak");
    }

    @After
    public void tearDown() throws Exception {
        this.renamedPropFile.renameTo(this.origPropFile);
    }

    @Test
    public final void testLanguageMap() {
        this.map = new LanguageMap();
        assertEquals("English", this.map.getLanguage("eng"));
    }

    @Test(expected = EresourceDatabaseException.class)
    public final void testMissingLanguagePropFile() {
        this.origPropFile.renameTo(this.renamedPropFile);
        this.map = new LanguageMap();
    }
}
