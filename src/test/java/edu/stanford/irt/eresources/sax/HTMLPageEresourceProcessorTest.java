package edu.stanford.irt.eresources.sax;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.helpers.DefaultHandler;


public class HTMLPageEresourceProcessorTest {
    
    private HTMLPageEresourceProcessor processor;

    @Before
    public void setUp() {
        String basePath = System.getProperty("user.dir") + "/src/test/resources/edu/stanford/irt/eresources/sax";
        this.processor = new HTMLPageEresourceProcessor(basePath, new DefaultHandler());
    }

    @Test
    public void testProcess() {
        this.processor.process();
    }
}
