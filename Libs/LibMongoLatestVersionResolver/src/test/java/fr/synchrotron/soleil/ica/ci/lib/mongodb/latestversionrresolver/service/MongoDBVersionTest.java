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
public class MongoDBVersionTest extends AbstractVersionTest {

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
        InputStream inputStream = MongoDBVersionTest.class.getResourceAsStream("artifacts.latest.data");
        final List<String> mongoDBDocs = IOUtils.readLines(inputStream);
        for (String mongoDBDoc : mongoDBDocs) {
            if (mongoDBDoc != null && !mongoDBDoc.trim().isEmpty()) {
                artifactsLatestCollection.insert(mongoDBDoc);
            }
        }
        inputStream.close();
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
    public void latestNotManagedVersionOrStatus() {

        Assert.assertEquals("X.Y.Z.build", resolveVersion("X.Y.Z.build"));
        Assert.assertEquals("X.Y.Z.BUILD", resolveVersion("X.Y.Z.BUILD"));

        Assert.assertEquals("X.Y.Z.integration", resolveVersion("X.Y.Z.integration"));
        Assert.assertEquals("X.Y.Z.INTEGRATION", resolveVersion("X.Y.Z.INTEGRATION"));

        Assert.assertEquals("X.Y.Z.release", resolveVersion("X.Y.Z.release"));
        Assert.assertEquals("X.Y.Z.RELEASE", resolveVersion("X.Y.Z.RELEASE"));

        Assert.assertEquals("X.Y.Z.anyMavenStatus", resolveVersion("X.Y.Z.anyMavenStatus"));
        Assert.assertEquals("X.Y.Z.ANYMAVENSTATUS", resolveVersion("X.Y.Z.ANYMAVENSTATUS"));
    }

    @Test
    public void latestBuildStatus() {
        Assert.assertEquals("1.7.BUILD", resolveVersion("name1", "latest.build"));
        Assert.assertEquals("1.6.INTEGRATION", resolveVersion("name2", "latest.build"));
        Assert.assertEquals("1.5.RELEASE", resolveVersion("name3", "latest.build"));
    }

    @Test
    public void latestIntegrationStatus() {
        Assert.assertEquals("1.6.INTEGRATION", resolveVersion("name1", "latest.integration"));
        Assert.assertEquals("1.6.INTEGRATION", resolveVersion("name2", "latest.integration"));
        Assert.assertEquals("1.5.RELEASE", resolveVersion("name3", "latest.integration"));
    }

    @Test
    public void latestReleaseStatus() {
        Assert.assertEquals("1.5.RELEASE", resolveVersion("name1", "latest.release"));
        Assert.assertEquals("1.5.RELEASE", resolveVersion("name2", "latest.release"));
        Assert.assertEquals("1.5.RELEASE", resolveVersion("name3", "latest.release"));
    }

    static private class InMemoryMongoDBDataSource implements MongoDBDataSource {
        @Override
        public DB getMongoDB() {
            return mongoDB;
        }
    }


}
