package edu.stanford.irt.eresources.logging;

import java.io.InterruptedIOException;
import java.nio.channels.ClosedByInterruptException;

import org.slf4j.LoggerFactory;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * A logging advisor that logs exceptions.
 *
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
         */
        public void afterThrowing(final Exception e) {
            if ((e instanceof ClosedByInterruptException) || (e instanceof InterruptedException)
                    || (e instanceof InterruptedIOException)) {
                LoggerFactory.getLogger(e.getClass()).warn(e.getMessage());
            } else {
                LoggerFactory.getLogger(e.getClass()).error(e.getMessage(), e);
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
