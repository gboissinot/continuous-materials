package fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.exception;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBException;

/**
 * @author Gregory Boissinot
 */
public class NoSuchElementException extends MongoDBException {

    public NoSuchElementException(String s) {
        super(s);
    }

}
