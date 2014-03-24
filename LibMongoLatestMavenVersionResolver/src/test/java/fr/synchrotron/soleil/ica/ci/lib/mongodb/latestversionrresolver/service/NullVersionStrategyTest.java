package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.ArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.NullArtifactRepository;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Boissinot
 */
public class NullVersionStrategyTest extends AbstractVersionStrategyTest {

    @Override
    protected ArtifactRepository getArtifactRepository() {
        return new NullArtifactRepository();
    }

    @Test
    public void testFixVersion() {
        Assert.assertEquals("X.Y.Z", resolveVersion("X.Y.Z"));
    }

    @Test
    public void integrationStatus() {
        Assert.assertEquals("X.Y.Z.integration", resolveVersion("X.Y.Z.integration"));
        Assert.assertEquals("X.Y.Z.INTEGRATION", resolveVersion("X.Y.Z.INTEGRATION"));
        Assert.assertEquals("latest.integration", resolveVersion("latest.integration"));
        Assert.assertEquals("latest.INTEGRATION", resolveVersion("latest.INTEGRATION"));
    }

    @Test
    public void testStatus() {
        Assert.assertEquals("X.Y.Z.test", resolveVersion("X.Y.Z.test"));
        Assert.assertEquals("X.Y.Z.TEST", resolveVersion("X.Y.Z.TEST"));
        Assert.assertEquals("latest.test", resolveVersion("latest.test"));
        Assert.assertEquals("latest.TEST", resolveVersion("latest.TEST"));
    }

    @Test
    public void releaseStatus() {
        Assert.assertEquals("X.Y.Z.release", resolveVersion("X.Y.Z.release"));
        Assert.assertEquals("X.Y.Z.RELEASE", resolveVersion("X.Y.Z.RELEASE"));
        Assert.assertEquals("latest.release", resolveVersion("latest.release"));
        Assert.assertEquals("latest.RELEASE", resolveVersion("latest.RELEASE"));
    }

    @Test
    public void otherStatus() {
        Assert.assertEquals("X.Y.Z.anyMavenStatus", resolveVersion("X.Y.Z.anyMavenStatus"));
        Assert.assertEquals("X.Y.Z.ANYMAVENSTATUS", resolveVersion("X.Y.Z.ANYMAVENSTATUS"));
        Assert.assertEquals("latest.anyLevercaseStatus", resolveVersion("latest.anyLevercaseStatus"));
        Assert.assertEquals("latest.ANYUPPERCASESTATUS", resolveVersion("latest.ANYUPPERCASESTATUS"));
    }

}
