package edu.stanford.irt.eresources.logging;

import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.channels.ClosedByInterruptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * A logging advisor that logs exceptions.
 * 
 * @author ceyates
 */
public class ExceptionAdvisor extends DefaultPointcutAdvisor {

    /**
     * logs at INFO level exceptions with stack trace.
     */
    public static class ExceptionAdvice implements ThrowsAdvice {

        /**
         * logs the exception
         * 
         * @param e
         *            the Exception
         * @throws Throwable
         *             if something bad happened
         */
        public void afterThrowing(final Exception e) {
            Logger log = LoggerFactory.getLogger(e.getClass());
            if ((e instanceof ClosedByInterruptException) || (e instanceof InterruptedException)
                    || (e instanceof InterruptedIOException)) {
                log.warn(e.getMessage());
            } else {
                log.error(e.getMessage(), e);
            }
        }
    }

    /** for Serializable. */
    private static final long serialVersionUID = 1L;

    /**
     * default constructor.
     */
    public ExceptionAdvisor() {
        super(new ExceptionAdvice());
    }
}
