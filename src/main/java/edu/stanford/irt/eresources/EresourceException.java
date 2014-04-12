package edu.stanford.irt.eresources;

public class EresourceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EresourceException(final String message) {
        super(message);
    }

    public EresourceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EresourceException(final Throwable cause) {
        super(cause);
    }
}
