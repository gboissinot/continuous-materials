package fr.synchrotron.soleil.ica.ci.lib.mongodb.util;

/**
 * @author Gregory Boissinot
 */
public class MongoDBException extends RuntimeException {

    public MongoDBException(String message) {
        super(message);
    }

    public MongoDBException(Throwable throwable) {
        super(throwable);
    }
}
