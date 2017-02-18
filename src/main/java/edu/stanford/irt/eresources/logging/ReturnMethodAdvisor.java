package edu.stanford.irt.eresources.logging;

import java.lang.reflect.Method;

import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * A logging advisor that logs at method return with method name and return values.
 *
 * @author ceyates
 */
public class ReturnMethodAdvisor extends DefaultPointcutAdvisor {

    /**
     * provides the advice code.
     */
    static class ReturnMethodAdvice implements AfterReturningAdvice {

        /**
         * logs at INFO level with the thread, object, method and return value.
         *
         * @param method
         *            the advised Method
         * @param params
         *            the Method's parameters
         * @param advisee
         *            the Object getting the advice
         * @param returnValue
         *            the Method's return value
         */
        @Override
        public void afterReturning(final Object returnValue, final Method method, final Object[] params,
                final Object advisee) {
            if ("handleEresource".equals(method.getName())) {
                return;
            }
            StringBuilder sb = new StringBuilder("return ").append(method.getName()).append("()")
                    .append(returnValue == null ? ";" : " = " + returnValue);
            LoggerFactory.getLogger(advisee.getClass()).info("{}", sb);
        }
    }

    /** for Serializable. */
    private static final long serialVersionUID = 1L;

    /**
     * default constructor.
     */
    public ReturnMethodAdvisor() {
        super(new ReturnMethodAdvice());
    }
}
