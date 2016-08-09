/**
 *
 */
package edu.stanford.irt.eresources;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.Test;

/**
 * @author ryanmax
 */
public class LimitedSMTPAppenderTest {

    LimitedSMTPAppender appender;

    @Before
    public void setUp() throws Exception {
        this.appender = new LimitedSMTPAppender();
        this.appender.setLimit(1);
        this.appender.setCycleSeconds(1);
        this.appender.setThreshold(Level.ERROR);
        this.appender.setTo("nobody");
        this.appender.setFrom("nobody");
        this.appender.setSubject("test");
        this.appender.setBufferSize(512);
        this.appender.setLayout(new PatternLayout("pattern"));
        this.appender.activateOptions();
    }

    @Test
    public final void test() throws Exception {
        assertTrue(this.appender.checkEntryConditions());
        assertFalse(this.appender.checkEntryConditions());
        assertFalse(this.appender.checkEntryConditions());
        Thread.sleep(1000);
        assertTrue(this.appender.checkEntryConditions());
    }
}
