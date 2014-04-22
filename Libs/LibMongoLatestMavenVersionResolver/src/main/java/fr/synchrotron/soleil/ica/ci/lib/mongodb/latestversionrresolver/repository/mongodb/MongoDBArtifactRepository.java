package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.mongodb;

import com.mongodb.*;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenInputArtifact;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.exception.MavenVersionResolverException;
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

    @Override
    public String getLatestVersion(MavenInputArtifact mavenInputArtifact) {

        assert (mavenInputArtifact != null) : ("A maven input artifact is required.");
        String version = mavenInputArtifact.getVersion();
        assert (version != null) : "A version for the input Maven artifact is required.";
        assert (version.startsWith(MavenInputArtifact.LATEST_KEYWORD)) : "A version must start with latest. is required.";

        //Get Maven status
        String mongoDBStatus;
        DeliveryMavenArtifactStatus deliveryMavenArtifactStatus = getMavenStatus(version);
        if (deliveryMavenArtifactStatus == null) {
            mongoDBStatus = version.substring(MavenInputArtifact.LATEST_KEYWORD.length());
            return getLastVersionFromStatus(mavenInputArtifact.getGroupId(), mavenInputArtifact.getArtifactId(), mongoDBStatus);
        } else {
            boolean stop = false;
            String lastVersionFromStatus = null;
            while (!stop) {
                mongoDBStatus = getMongoDBArtifactStatus(deliveryMavenArtifactStatus);
                lastVersionFromStatus = getLastVersionFromStatus(mavenInputArtifact.getGroupId(), mavenInputArtifact.getArtifactId(), mongoDBStatus);
                if (lastVersionFromStatus != null || deliveryMavenArtifactStatus.getNextStatus() == -1) {
                    stop = true;
                } else {
                    deliveryMavenArtifactStatus = getDeliveryMavenArtifactStatusFromId(deliveryMavenArtifactStatus.getNextStatus());
                }
            }

            return lastVersionFromStatus;
        }
    }

    private DeliveryMavenArtifactStatus getDeliveryMavenArtifactStatusFromId(int statusId) {
        DeliveryMavenArtifactStatus[] statuses = DeliveryMavenArtifactStatus.values();
        for (DeliveryMavenArtifactStatus status : statuses) {
            if (statusId == status.getId()) {
                return status;
            }
        }
        throw new MavenVersionResolverException(String.format("Can't compute the status from the status id %s", statusId));
    }

    private String getLastVersionFromStatus(String requestedGroupId, String requestedArtifactId, String requestedStatus) {

        String latestVersion = null;
        DBCursor cursor = null;
        try {
            DB mongoDB = mongoDBDatasource.getMongoDB();

            DBCollection coll = mongoDB.getCollection(MONGODB_ARTIFACTS_LATEST_COLLECTION);
            BasicDBObject idDoc =
                    new BasicDBObject("org", requestedGroupId)
                            .append("name", requestedArtifactId)
                            .append("type", "binary")
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

    private String getMongoDBArtifactStatus(DeliveryMavenArtifactStatus mavenArtifactStatus) {

        if (mavenArtifactStatus != null) {
            MavenStatusMongoDBMapping[] values = MavenStatusMongoDBMapping.values();
            for (MavenStatusMongoDBMapping mapping : values) {
                if (mapping.getMavenStatusVersion().equalsIgnoreCase(mavenArtifactStatus.getValue())) {
                    return mapping.getMongoDBStatus();
                }
            }
        }

        throw new MavenVersionResolverException(String.format("Can't compute the status from the maven version %s", mavenArtifactStatus));
    }

    private DeliveryMavenArtifactStatus getMavenStatus(String mavenVersion) {

        String upperCasedMavenVersion = mavenVersion.toUpperCase();
        DeliveryMavenArtifactStatus[] statuses = DeliveryMavenArtifactStatus.values();
        for (DeliveryMavenArtifactStatus status : statuses) {
            if (upperCasedMavenVersion.endsWith(status.getValue())) {
                return status;
            }
        }

        return null;
    }


    private enum DeliveryMavenArtifactStatus {

        INTEGRATION(1, "INTEGRATION", 2),
        TEST(2, "TEST", 3),
        RELEASE(3, "RELEASE", -1);

        private final int id;
        private final String value;
        private final int nextStatus;

        DeliveryMavenArtifactStatus(int id, String value, int nextStatus) {
            this.id = id;
            this.value = value;
            this.nextStatus = nextStatus;
        }

        public int getId() {
            return id;
        }

        public String getValue() {
            return value;
        }

        public int getNextStatus() {
            return nextStatus;
        }
    }


    private enum MavenStatusMongoDBMapping {

        INTEGRATION("INTEGRATION", "INTEGRATION"),
        TEST("TEST", "TEST"),
        RELEASE("RELEASE", "RELEASE");

        private final String mavenStatusVersion;
        private final String mongoDBStatus;

        MavenStatusMongoDBMapping(String mavenStatusVersion, String mongoDBStatus) {
            this.mavenStatusVersion = mavenStatusVersion;
            this.mongoDBStatus = mongoDBStatus;
        }

        public String getMavenStatusVersion() {
            return mavenStatusVersion;
        }

        public String getMongoDBStatus() {
            return mongoDBStatus;
        }
    }
}
