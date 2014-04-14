package fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.exception;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBException;

/**
 * @author Gregory Boissinot
 */
public class NoSuchElementException extends MongoDBException {

    public NoSuchElementException() {
    }

    public NoSuchElementException(String s) {
        super(s);
    }

    public NoSuchElementException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NoSuchElementException(Throwable throwable) {
        super(throwable);
    }
}
