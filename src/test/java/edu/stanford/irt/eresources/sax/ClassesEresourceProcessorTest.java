package edu.stanford.irt.eresources.sax;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.helpers.DefaultHandler;


public class ClassesEresourceProcessorTest {
    
    private ClassesEresourceProcessor processor;

    @Before
    public void setUp() {
        URL url = getClass().getResource("classes.xml");
        this.processor = new ClassesEresourceProcessor(url, new DefaultHandler());
    }

    @Test
    public void testProcess() {
        this.processor.process();
    }
}
