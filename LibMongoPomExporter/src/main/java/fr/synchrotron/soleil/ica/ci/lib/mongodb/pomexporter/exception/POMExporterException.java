package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.exception;

/**
 * @author Gregory Boissinot
 */
public class POMExporterException extends RuntimeException {

    public POMExporterException() {
    }

    public POMExporterException(String message) {
        super(message);
    }

    public POMExporterException(String message, Throwable cause) {
        super(message, cause);
    }

    public POMExporterException(Throwable cause) {
        super(cause);
    }

    public POMExporterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
