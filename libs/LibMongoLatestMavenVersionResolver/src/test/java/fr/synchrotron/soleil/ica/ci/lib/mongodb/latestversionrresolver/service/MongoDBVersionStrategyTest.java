package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.ArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.mongodb.MongoDBArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.apache.commons.io.IOUtils;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class MongoDBVersionStrategyTest extends AbstractVersionStrategyTest {

    private static DB mongoDB;

    @BeforeClass
    public static void setup() throws IOException {
        Fongo fongo = new Fongo("testMongoServer");
        mongoDB = fongo.getDB("repo");
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection artifactsLatestCollection =
                jongo.getCollection(MongoDBArtifactRepository.MONGODB_ARTIFACTS_LATEST_COLLECTION);
        loadMongoDBData(artifactsLatestCollection);
    }

    @AfterClass
    public static void cleanupDatabase() throws IOException {
        if (mongoDB != null) {
            mongoDB.dropDatabase();
        }
    }

    private static void loadMongoDBData(MongoCollection artifactsLatestCollection) throws IOException {
        InputStream inputStream = MongoDBVersionStrategyTest.class.getResourceAsStream("artifacts.latest.data");
        final List<String> mongoDBDocs = IOUtils.readLines(inputStream);
        for (String mongoDBDoc : mongoDBDocs) {
            if (mongoDBDoc != null) {
                artifactsLatestCollection.insert(mongoDBDoc);
            }
        }
        inputStream.close();
    }

    static private class InMemoryMongoDBDataSource implements MongoDBDataSource {
        @Override
        public DB getMongoDB() {
            return mongoDB;
        }
    }

    @Override
    protected ArtifactRepository getArtifactRepository() {
        return new MongoDBArtifactRepository(new InMemoryMongoDBDataSource());
    }

    @Test
    public void testFixVersion() {
        Assert.assertEquals("X.Y.Z", resolveVersion("X.Y.Z"));
    }

    @Test
    public void integrationStatus() {
        Assert.assertEquals("X.Y.Z.integration", resolveVersion("X.Y.Z.integration"));
        Assert.assertEquals("X.Y.Z.INTEGRATION", resolveVersion("X.Y.Z.INTEGRATION"));
        Assert.assertEquals("1.7.INTEGRATION", resolveVersion("latest.integration"));
        Assert.assertEquals("1.7.INTEGRATION", resolveVersion("latest.INTEGRATION"));
    }

    @Test
    public void testStatus() {
        Assert.assertEquals("X.Y.Z.test", resolveVersion("X.Y.Z.test"));
        Assert.assertEquals("X.Y.Z.TEST", resolveVersion("X.Y.Z.TEST"));
        Assert.assertEquals("1.6.TEST", resolveVersion("latest.test"));
        Assert.assertEquals("1.6.TEST", resolveVersion("latest.TEST"));
    }

    @Test
    public void releaseStatus() {
        Assert.assertEquals("X.Y.Z.release", resolveVersion("X.Y.Z.release"));
        Assert.assertEquals("X.Y.Z.RELEASE", resolveVersion("X.Y.Z.RELEASE"));
        Assert.assertEquals("1.5.RELEASE", resolveVersion("latest.release"));
        Assert.assertEquals("1.5.RELEASE", resolveVersion("latest.RELEASE"));
    }

    @Test
    public void otherStatus() {
        Assert.assertEquals("X.Y.Z.anyMavenStatus", resolveVersion("X.Y.Z.anyMavenStatus"));
        Assert.assertEquals("X.Y.Z.ANYMAVENSTATUS", resolveVersion("X.Y.Z.ANYMAVENSTATUS"));
        Assert.assertEquals("1.5.anyLevercaseStatus", resolveVersion("latest.anyLevercaseStatus"));
        Assert.assertEquals("1.5.ANYUPPERCASESTATUS", resolveVersion("latest.ANYUPPERCASESTATUS"));
    }

    @Test
    public void testArtifactIdWithNoIntegration() {
        Assert.assertEquals("1.5.RELEASE", resolveVersion("testGroupId", "testArtifactIdWithNoIntegration", "latest.integration"));
    }


}
