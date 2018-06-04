package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;

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

    @Test(expected = IllegalArgumentException.class)
    public final void testNull() {
        this.processor.setStartDate(null);
    }

    @Test
    public final void testProcessor() {
        LocalDateTime ldt = LocalDateTime.now();
        long time = ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        this.processor.setStartDate(ldt);
        assertEquals(time, this.processor.getStartTime());
    }
}
