package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.mongodb;

import com.mongodb.*;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.ArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;

/**
 * @author Gregory Boissinot
 */
public class MongoDBArtifactRepository implements ArtifactRepository {

    public static final String MONGODB_ARTIFACTS_LATEST_COLLECTION = "artifacts.latest";
    private final MongoDBDataSource mongoDBDatasource;

    public MongoDBArtifactRepository(MongoDBDataSource mongoDBDatasource) {
        this.mongoDBDatasource = mongoDBDatasource;
    }

    @Override
    public String getName() {
        return "MongoDB Repository";
    }

    public String getLatestVersion(String requestedOrg,
                                 String requestedName,
                                 String requestedType,
                                 String requestedStatus) {

        String latestVersion = null;
        DBCursor cursor = null;
        try {
            DB mongoDB = mongoDBDatasource.getMongoDB();

            DBCollection coll = mongoDB.getCollection(MONGODB_ARTIFACTS_LATEST_COLLECTION);
            BasicDBObject idDoc =
                    new BasicDBObject("org", requestedOrg)
                            .append("name", requestedName)
                            .append("type", requestedType)
                            .append("status", requestedStatus);

            BasicDBObject query = new BasicDBObject("_id", idDoc);
            cursor = coll.find(query);

            while (cursor.hasNext()) {
                BasicDBObject doc = (BasicDBObject) cursor.next();
                latestVersion = doc.getString("value");
            }

            DBObject err = mongoDB.getLastError();
            if (!((CommandResult) err).ok()) {
                throw ((CommandResult) err).getException();
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return latestVersion;
    }
}
