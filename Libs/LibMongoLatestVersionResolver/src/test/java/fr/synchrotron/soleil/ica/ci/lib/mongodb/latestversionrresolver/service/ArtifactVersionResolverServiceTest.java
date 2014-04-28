package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.ArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.workflow.Workflow;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

/**
 * @author Gregory Boissinot
 */
public class ArtifactVersionResolverServiceTest {

    @Test(expected = NullPointerException.class)
    public void tesNullArtifactRepositoryConstructorOneParameter() {
        new ArtifactVersionResolverService(null);
    }

    @Test(expected = NullPointerException.class)
    public void tesNullArtifactRepositoryConstructorTwoParameter() {
        new ArtifactVersionResolverService(null, any(Workflow.class));
    }

    @Test(expected = NullPointerException.class)
    public void tesNullWorkflowConstructorTwoParameter() {
        new ArtifactVersionResolverService(any(ArtifactRepository.class), null);
    }

    @Test(expected = NullPointerException.class)
    public void getLastVersionWithOrgNull() {
        ArtifactVersionResolverService resolverService =
                new ArtifactVersionResolverService(any(ArtifactRepository.class), any(Workflow.class));
        resolverService.getLatestVersion(null, anyString(), anyString());
    }

    @Test(expected = NullPointerException.class)
    public void getLastVersionWithNameNull() {
        ArtifactVersionResolverService resolverService =
                new ArtifactVersionResolverService(any(ArtifactRepository.class), any(Workflow.class));
        resolverService.getLatestVersion(anyString(), null, anyString());
    }

    public void getLastVersionWithStatusNull() {
        ArtifactVersionResolverService resolverService =
                new ArtifactVersionResolverService(any(ArtifactRepository.class), Workflow.DEFAULT_WORKFLOW_STATUS);
        resolverService.getLatestVersion(anyString(), anyString(), null);
    }

}
