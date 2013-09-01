package edu.stanford.irt.eresources;

public class EresourceDatabaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EresourceDatabaseException(final String message) {
        super(message);
    }

    public EresourceDatabaseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EresourceDatabaseException(final Throwable cause) {
        super(cause);
    }
}
