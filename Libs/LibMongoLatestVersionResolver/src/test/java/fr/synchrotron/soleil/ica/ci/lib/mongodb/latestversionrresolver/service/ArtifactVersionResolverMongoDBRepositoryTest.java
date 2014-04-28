package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service.data.MongoDBDataRepository;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public class ArtifactVersionResolverMongoDBRepositoryTest {

    private static MongoDBDataRepository mongoDBDataRepository = new MongoDBDataRepository();
    private static ArtifactVersionResolverService resolverService;

    @BeforeClass
    public static void setupData() throws IOException {
        mongoDBDataRepository.setup();
        resolverService = new ArtifactVersionResolverService(mongoDBDataRepository.getArtifactRepository());
    }

    public static void cleanup() throws IOException {
        mongoDBDataRepository.cleanupDatabase();
    }

    private String getLatest(String org, String name, String status) {
        return resolverService.getLatestVersion(org, name, status);
    }

    @Test
    public void latestBuildStatus() {
        Assert.assertEquals("1.7.BUILD", getLatest("org1", "name1", "build"));
        Assert.assertEquals("1.6.INTEGRATION", getLatest("org2", "name2", "build"));
        Assert.assertEquals("1.5.RELEASE", getLatest("org3", "name3", "build"));
    }

    @Test
    public void latestIntegrationStatus() {
        Assert.assertEquals("1.6.INTEGRATION", getLatest("org1", "name1", "integration"));
        Assert.assertEquals("1.6.INTEGRATION", getLatest("org2", "name2", "integration"));
        Assert.assertEquals("1.5.RELEASE", getLatest("org3", "name3", "integration"));
    }

    @Test
    public void latestReleaseStatus() {
        Assert.assertEquals("1.5.RELEASE", getLatest("org1", "name1", "release"));
        Assert.assertEquals("1.5.RELEASE", getLatest("org2", "name2", "release"));
        Assert.assertEquals("1.5.RELEASE", getLatest("org3", "name3", "release"));
    }

}
