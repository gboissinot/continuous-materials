package fr.synchrotron.soleil.ica.ci.lib.mongodb.util;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class BasicMongoDBDataSource implements MongoDBDataSource {

    private static final String DEFAULT_MONGODB_HOST = "localhost";
    private static final int DEFAULT_MONGODB_PORT = 27017;
    private static final String DEFAULT_MONGODB_DBNAME = "artifactRepository";

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

    public BasicMongoDBDataSource(List mongoDBInstances) {
        initInstances(mongoDBInstances, DEFAULT_MONGODB_DBNAME);
    }

    public BasicMongoDBDataSource(List mongoDBInstances, String mongoDBName) {
        initInstances(mongoDBInstances, mongoDBName);
    }

    private void init(String mongoHost, int mongoPort, String mongoDBName) {
        try {
            mongo = new MongoClient(mongoHost, mongoPort);
        } catch (UnknownHostException ue) {
            throw new MongoDBException(ue);
        }
        this.mongoDBName = mongoDBName;
    }

    private void initInstances(List mongoDBInstances, String mongoDBName) {
        List serverAddresses = new ArrayList();
        for (int i = 0; i < mongoDBInstances.size(); i++) {
            MongoDBInstance mongoDBInstance = (MongoDBInstance) mongoDBInstances.get(i);
            try {
                serverAddresses.add(new ServerAddress(mongoDBInstance.getHost(), mongoDBInstance.getPort()));
            } catch (UnknownHostException ue) {
                throw new MongoDBException(ue);
            }
        }
        mongo = new MongoClient(serverAddresses);
        this.mongoDBName = mongoDBName;
    }

    public DB getMongoDB() {
        return mongo.getDB(mongoDBName);
    }

    public DB getMongoDB(String dbName) {
        return mongo.getDB(dbName);
    }

}