package fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.exception;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBException;

/**
 * @author Gregory Boissinot
 */
public class DuplicateElementException extends MongoDBException {

    public DuplicateElementException() {
    }

    public DuplicateElementException(String s) {
        super(s);
    }

    public DuplicateElementException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DuplicateElementException(Throwable throwable) {
        super(throwable);
    }
}
