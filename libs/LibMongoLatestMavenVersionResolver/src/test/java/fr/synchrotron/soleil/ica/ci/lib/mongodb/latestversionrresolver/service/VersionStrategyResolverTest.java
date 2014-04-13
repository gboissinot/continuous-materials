package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.NullArtifactRepository;
import org.junit.Test;

/**
 * @author Gregory Boissinot
 */
public class VersionStrategyResolverTest {

    @Test(expected = NullPointerException.class)
    public void nullArtifactRepository() {
        new VersionStrategyResolver(null);
    }

    @Test(expected = NullPointerException.class)
    public void resolveNullMavenInputArtifact() {
        VersionStrategyResolver versionStrategyResolver = new VersionStrategyResolver(new NullArtifactRepository());
        versionStrategyResolver.resolveMavenArtifact(null);
    }

}
