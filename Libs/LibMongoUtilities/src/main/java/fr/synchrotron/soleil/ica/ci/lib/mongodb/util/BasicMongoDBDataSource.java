package fr.synchrotron.soleil.ica.ci.lib.mongodb.util;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Gregory Boissinot
 */
public class BasicMongoDBDataSource implements MongoDBDataSource {

    public static final String DEFAULT_MONGODB_HOST = "localhost";
    public static final int DEFAULT_MONGODB_PORT = 27017;
    public static final String DEFAULT_MONGODB_DBNAME = "artifactRepository";

    private MongoClient mongo;
    private String mongoDBName;

    public BasicMongoDBDataSource() {
        init(DEFAULT_MONGODB_HOST, DEFAULT_MONGODB_PORT, DEFAULT_MONGODB_DBNAME);
    }

    public BasicMongoDBDataSource(String mongoHost, int mongoPort) {
        init(mongoHost, mongoPort, DEFAULT_MONGODB_DBNAME);
    }

    public BasicMongoDBDataSource(String mongoHost, int mongoPort, String mongoDBName) {
        init(mongoHost, mongoPort, mongoDBName);
    }

    public BasicMongoDBDataSource(Properties properties) {
        init(properties.getProperty("mongo.host"),
                Integer.parseInt(properties.getProperty("mongo.port")),
                properties.getProperty("mongo.dbname"));
    }

    public BasicMongoDBDataSource(Map<String, String> properties) {
        init(properties.get("mongoHost"),
                Integer.parseInt(properties.get("mongoPort")),
                properties.get(" mongoDbName"));
    }

    public BasicMongoDBDataSource(List mongoDBInstances) {
        initInstances(mongoDBInstances, DEFAULT_MONGODB_DBNAME);
    }

    public BasicMongoDBDataSource(List mongoDBInstances, String mongoDBName) {
        initInstances(mongoDBInstances, mongoDBName);
    }

    private void init(String mongoHost, int mongoPort, String mongoDBName) {
        checkParameters(mongoHost, mongoPort, mongoDBName);
        try {
            mongo = new MongoClient(mongoHost, mongoPort);
        } catch (UnknownHostException ue) {
            throw new MongoDBException(ue);
        }
        this.mongoDBName = mongoDBName;
    }

    private void initInstances(List<MongoDBInstance> mongoDBInstances, String mongoDBName) {
        List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
        for (MongoDBInstance mongoDBInstance : mongoDBInstances) {
            try {
                final String mongoHost = mongoDBInstance.getHost();
                final int mongoPort = mongoDBInstance.getPort();
                checkParameters(mongoHost, mongoPort, mongoDBName);
                serverAddresses.add(new ServerAddress(mongoHost, mongoPort));
            } catch (UnknownHostException ue) {
                throw new MongoDBException(ue);
            }
        }
        mongo = new MongoClient(serverAddresses);
        this.mongoDBName = mongoDBName;
    }

    private void checkParameters(Object... parameters) {

        if (parameters.length == 0) {
            return;
        }

        for (Object parameter : parameters) {
            if (parameter == null) {
                throw new NullPointerException("All parameters are requited");
            }

            if (parameter instanceof Number) {
                if (parameter.equals(-1)) {
                    throw new NullPointerException("All parameters are requited");
                }
            }
        }
    }

    public DB getMongoDB() {
        return mongo.getDB(mongoDBName);
    }

    public DB getMongoDB(String dbName) {
        return mongo.getDB(dbName);
    }

}