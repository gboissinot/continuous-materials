package fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Gregory Boissinot
 */
public class MongoDBUtilities {

    public BasicMongoDBDataSource getBasicMongoDBDataSource(JsonObject config) {
        return new BasicMongoDBDataSource(
                config.getString("mongo.host"),
                Integer.parseInt(config.getString("mongo.port")),
                config.getString("mongo.dbname"));
    }
}
