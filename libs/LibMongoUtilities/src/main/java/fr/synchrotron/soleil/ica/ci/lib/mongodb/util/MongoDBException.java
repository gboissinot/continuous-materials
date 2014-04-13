package fr.synchrotron.soleil.ica.ci.lib.mongodb.util;

/**
 * @author Gregory Boissinot
 */
public class MongoDBException extends RuntimeException {

    public MongoDBException() {
    }

    public MongoDBException(String s) {
        super(s);
    }

    public MongoDBException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public MongoDBException(Throwable throwable) {
        super(throwable);
    }
}
