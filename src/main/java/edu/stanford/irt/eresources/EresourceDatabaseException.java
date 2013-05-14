package edu.stanford.irt.eresources;


public class EresourceDatabaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EresourceDatabaseException(String message) {
        super(message);
    }

    public EresourceDatabaseException(Throwable cause) {
        super(cause);
    }

    public EresourceDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
