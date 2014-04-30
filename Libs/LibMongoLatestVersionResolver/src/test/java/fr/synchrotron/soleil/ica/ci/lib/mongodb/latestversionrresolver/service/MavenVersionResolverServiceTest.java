package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.NullArtifactRepository;
import org.junit.Test;

/**
 * @author Gregory Boissinot
 */
public class MavenVersionResolverServiceTest {

    @Test(expected = NullPointerException.class)
    public void getLatestNullGroupId() {
        final MavenVersionResolverService mavenVersionResolverService = new MavenVersionResolverService(new ArtifactVersionResolverService(new NullArtifactRepository()));
        mavenVersionResolverService.getLatestArtifact(null, "anArtifactId");
    }

    @Test(expected = NullPointerException.class)
    public void getLatestNullArtifactId() {
        final MavenVersionResolverService mavenVersionResolverService = new MavenVersionResolverService(new ArtifactVersionResolverService(new NullArtifactRepository()));
        mavenVersionResolverService.getLatestArtifact("anArtifactId", null);
    }

    @Test(expected = NullPointerException.class)
    public void resolveArtifactNullMavenInputArtifact() {
        final MavenVersionResolverService mavenVersionResolverService = new MavenVersionResolverService(new ArtifactVersionResolverService(new NullArtifactRepository()));
        mavenVersionResolverService.resolveArtifact(null);
    }

}
