package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenInputArtifact;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenOutputArtifact;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.ArtifactRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * @author Gregory Boissinot
 */
@RunWith(MockitoJUnitRunner.class)
public class MavenVersionResolverServiceTest {

    private static MavenVersionResolverService versionResolverService;

    @Mock
    private static ArtifactRepository artifactRepositoryMock;

    @Mock
    private MavenInputArtifact mavenInputArtifactMock;

    @Before
    public void setup() {
        versionResolverService = new MavenVersionResolverService(artifactRepositoryMock);
    }

    @Test(expected = NullPointerException.class)
    public void nullArtifactoryRepository() {
        new MavenVersionResolverService(null);
    }

    @Test(expected = NullPointerException.class)
    public void getLatestArtifactNullGroupId() {
        Assert.assertNull(versionResolverService.getLatestArtifact(null, "AnArtifactIdValue"));
    }

    @Test(expected = NullPointerException.class)
    public void getLatestArtifactArtifactId() {
        Assert.assertNull(versionResolverService.getLatestArtifact("AGroupIdValue", null));
    }

    @Test
    public void getLatestWithElements() {
        final String resolvedVersion =
                versionResolverService.getLatestArtifact("AnyGroupIdValue", "AnyArtifactIdValue");
        Assert.assertNotNull(resolvedVersion);
    }

    @Test(expected = NullPointerException.class)
    public void resolveNullMavenInputArtifact() {
        Assert.assertNull(versionResolverService.resolveArtifact(null));
    }

    @Test
    public void resolveArtifactWithOneElement() {
        when(mavenInputArtifactMock.getGroupId()).thenReturn("AnyGroupIdValue");
        when(mavenInputArtifactMock.getArtifactId()).thenReturn("AnyArtifactIdValue");
        when(mavenInputArtifactMock.getVersion()).thenReturn("AnyVersionValue");
        final MavenOutputArtifact mavenOutputArtifact =
                versionResolverService.resolveArtifact(mavenInputArtifactMock);
        Assert.assertNotNull(mavenOutputArtifact);
        Assert.assertNotNull(mavenOutputArtifact.getGroupId());
        Assert.assertNotNull(mavenOutputArtifact.getArtifactId());
        Assert.assertNotNull(mavenOutputArtifact.getVersion());
    }

}
