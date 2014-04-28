package fr.synchrotron.soleil.ica.ci.lib.mongodb.util;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Gregory Boissinot
 */
public class BasicMongoDBDataSourceTest {

    @Test
    public void testDefault() {
        BasicMongoDBDataSource mongoDBDataSource = new BasicMongoDBDataSource();
        final DB mongoDB = mongoDBDataSource.getMongoDB();
        assertNotNull(mongoDB);
        assertEquals(BasicMongoDBDataSource.DEFAULT_MONGODB_DBNAME, mongoDB.getName());
        final Mongo mongo = mongoDB.getMongo();
        assertNotNull(mongo);
        final ServerAddress address = mongo.getAddress();
        assertNotNull(address);
        assertEquals(BasicMongoDBDataSource.DEFAULT_MONGODB_HOST, address.getHost());
        assertEquals(BasicMongoDBDataSource.DEFAULT_MONGODB_PORT, address.getPort());
    }

    @Test
    public void testInstanceWith2Parameters() {
        final String localhost = "localhost";
        final int port = 2000;
        BasicMongoDBDataSource mongoDBDataSource = new BasicMongoDBDataSource(localhost, port);
        final DB mongoDB = mongoDBDataSource.getMongoDB();
        assertNotNull(mongoDB);
        assertEquals(BasicMongoDBDataSource.DEFAULT_MONGODB_DBNAME, mongoDB.getName());
        final Mongo mongo = mongoDB.getMongo();
        assertNotNull(mongo);
        final ServerAddress address = mongo.getAddress();
        assertNotNull(address);
        assertEquals(localhost, address.getHost());
        assertEquals(port, address.getPort());
    }

    @Test
    public void testInstanceWith3Parameters() {
        final String dbName = "myDbName";
        final String localhost = "localhost";
        final int port = 2000;
        BasicMongoDBDataSource mongoDBDataSource = new BasicMongoDBDataSource(localhost, port, dbName);
        final DB mongoDB = mongoDBDataSource.getMongoDB();
        assertNotNull(mongoDB);
        assertEquals(dbName, mongoDB.getName());
        final Mongo mongo = mongoDB.getMongo();
        assertNotNull(mongo);
        final ServerAddress address = mongo.getAddress();
        assertNotNull(address);
        assertEquals(localhost, address.getHost());
        assertEquals(port, address.getPort());
    }

    @Test
    public void testInstanceWithListMongoInstancesAsParameters() {
        MongoDBInstance mongoDBInstance1 = new MongoDBInstance("localhost", 27001);
        MongoDBInstance mongoDBInstance2 = new MongoDBInstance("localhost", 27002);
        final String dbName = "myDbName";
        BasicMongoDBDataSource mongoDBDataSource =
                new BasicMongoDBDataSource(Arrays.asList(mongoDBInstance1, mongoDBInstance2), dbName);
        final DB mongoDB = mongoDBDataSource.getMongoDB();
        assertNotNull(mongoDB);
        assertEquals(dbName, mongoDB.getName());
        final Mongo mongo = mongoDB.getMongo();
        assertNotNull(mongo);

        final List<ServerAddress> serverAddressList = mongo.getAllAddress();
        assertNotNull(serverAddressList);
        assertEquals(2, serverAddressList.size());

        ServerAddress serverAddress1 = serverAddressList.get(0);
        assertEquals(mongoDBInstance1.getHost(), serverAddress1.getHost());
        assertEquals(mongoDBInstance1.getPort(), serverAddress1.getPort());

        ServerAddress serverAddress2 = serverAddressList.get(1);
        assertEquals(mongoDBInstance2.getHost(), serverAddress2.getHost());
        assertEquals(mongoDBInstance2.getPort(), serverAddress2.getPort());
    }

}
