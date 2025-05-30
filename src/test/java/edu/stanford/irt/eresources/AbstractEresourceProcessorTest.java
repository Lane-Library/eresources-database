package edu.stanford.irt.eresources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractEresourceProcessorTest {

    class EresourceProcessorTest extends AbstractEresourceProcessor {

        @Override
        public void process() {
            // TODO Auto-generated method stub
        }
    }

    EresourceProcessorTest processor;

    @BeforeEach
    void setUp() {
        this.processor = new EresourceProcessorTest();
    }

    @Test
    final void testNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.processor.setStartDate(null);
        });
    }

    @Test
    final void testProcessor() {
        LocalDateTime ldt = LocalDateTime.now();
        long time = ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        this.processor.setStartDate(ldt);
        assertEquals(time, this.processor.getStartTime());
    }
}
