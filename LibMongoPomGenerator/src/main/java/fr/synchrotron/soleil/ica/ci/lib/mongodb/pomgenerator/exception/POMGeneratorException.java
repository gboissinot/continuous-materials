package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomgenerator.exception;

/**
 * @author Gregory Boissinot
 */
public class POMGeneratorException extends RuntimeException {

    public POMGeneratorException() {
    }

    public POMGeneratorException(String message) {
        super(message);
    }

    public POMGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public POMGeneratorException(Throwable cause) {
        super(cause);
    }

    public POMGeneratorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
