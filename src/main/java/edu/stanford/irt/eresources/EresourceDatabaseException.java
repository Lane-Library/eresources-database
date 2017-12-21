package edu.stanford.irt.eresources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EresourceDatabaseException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(EresourceDatabaseException.class);

    private static final long serialVersionUID = 1L;

    public EresourceDatabaseException(final String message) {
        super(message);
        log.error(message);
    }

    public EresourceDatabaseException(final String message, final Throwable cause) {
        super(message, cause);
        log.error(message, cause);
    }

    public EresourceDatabaseException(final Throwable cause) {
        super(cause);
        log.error("EresourceDatabaseException", cause);
    }
}
