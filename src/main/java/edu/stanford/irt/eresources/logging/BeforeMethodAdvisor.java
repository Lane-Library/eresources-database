package edu.stanford.irt.eresources.logging;

import java.lang.reflect.Method;

import org.slf4j.LoggerFactory;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * A logging advisor that logs at method entry with method name and parameter values.
 *
 * @author ceyates
 */
public class BeforeMethodAdvisor extends DefaultPointcutAdvisor {

    /**
     * provides the advice code.
     */
    static class BeforeMethodAdvice implements MethodBeforeAdvice {

        private boolean logEresource;

        /**
         * logs at INFO level with the thread, object, method and parameters.
         *
         * @param method
         *            the advised Method
         * @param params
         *            the Method's parameters
         * @param advisee
         *            the Object getting the advice
         */
        @Override
        public void before(final Method method, final Object[] params, final Object advisee) {
            if (!this.logEresource && "handleEresource".equals(method.getName())) {
                return;
            }
            StringBuilder sb = new StringBuilder("enter ").append(method.getName()).append("(");
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    if (params[i] instanceof String) {
                        sb.append('\'').append(params[i]).append('\'');
                    } else {
                        sb.append(params[i]);
                    }
                    if (i < params.length - 1) {
                        sb.append(',');
                    }
                }
            }
            sb.append(");");
            LoggerFactory.getLogger(advisee.getClass()).info("{}", sb.toString());
        }
    }

    /** for Serializable. */
    private static final long serialVersionUID = 1L;

    /**
     * default constructor.
     */
    public BeforeMethodAdvisor() {
        super(new BeforeMethodAdvice());
    }

    public void setLogEresource(final boolean logEresource) {
        ((BeforeMethodAdvice) getAdvice()).logEresource = logEresource;
    }
}
