package edu.stanford.irt.eresources;

import static org.junit.Assert.assertSame;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class AbstractEresourceProcessorTest {

    private AbstractEresourceProcessor processor;

    @Before
    public void setUp() {
        this.processor = new AbstractEresourceProcessor() {

            @Override
            public void process() {
            }
        };
    }

    @Test
    public void testSetStartDate() {
        this.processor.setStartDate(new Date(1L));
        assertSame(1L, this.processor.getStartTime());
    }
}
