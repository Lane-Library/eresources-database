package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class AbstractEresourceProcessorTest {

    public class EresourceProcessorTest extends AbstractEresourceProcessor {

        @Override
        public void process() {
            // TODO Auto-generated method stub
        }
    }

    EresourceProcessorTest processor;

    @Before
    public void setUp() throws Exception {
        this.processor = new EresourceProcessorTest();
    }

    @Test
    public final void test() {
        Date d = new Date();
        long time = d.getTime();
        this.processor.setStartDate(d);
        assertEquals(time, this.processor.getStartTime());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testNull() {
        this.processor.setStartDate(null);
    }
}
