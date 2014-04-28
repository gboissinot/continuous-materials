package fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.exception;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBException;

/**
 * @author Gregory Boissinot
 */
public class DuplicateElementException extends MongoDBException {

    public DuplicateElementException(String s) {
        super(s);
    }

}
