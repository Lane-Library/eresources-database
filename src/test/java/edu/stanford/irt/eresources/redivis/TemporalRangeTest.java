package edu.stanford.irt.eresources.redivis;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TemporalRangeTest {

    @Test
    public final void testGetDisplayRange() {
        TemporalRange tr = new TemporalRange(null, null, null);
        assertTrue(tr.getDisplayRange().isEmpty());
    }
}
