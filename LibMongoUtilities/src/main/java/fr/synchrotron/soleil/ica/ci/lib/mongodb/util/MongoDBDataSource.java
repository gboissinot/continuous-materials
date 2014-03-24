package fr.synchrotron.soleil.ica.ci.lib.mongodb.util;

import com.mongodb.DB;

/**
 * @author Gregory Boissinot
 */
public interface MongoDBDataSource {

    public abstract DB getMongoDB();
}