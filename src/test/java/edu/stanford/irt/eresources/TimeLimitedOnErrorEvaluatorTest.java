package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class TimeLimitedOnErrorEvaluatorTest {

    TimeLimitedOnErrorEvaluator evaluator;

    ILoggingEvent event;

    Level level;

    @Before
    public void setUp() throws Exception {
        this.evaluator = new TimeLimitedOnErrorEvaluator();
        this.event = mock(ILoggingEvent.class);
    }

    @Test
    public final void testEvaluate() throws Exception {
        this.evaluator.setMessageLimit(2);
        this.evaluator.setTimeLimitMinutes(10);
        expect(this.event.getLevel()).andReturn(Level.ERROR).anyTimes();
        replay(this.event);
        assertTrue(this.evaluator.evaluate(this.event));
        assertTrue(this.evaluator.evaluate(this.event));
        assertFalse(this.evaluator.evaluate(this.event));
        verify(this.event);
    }
}
