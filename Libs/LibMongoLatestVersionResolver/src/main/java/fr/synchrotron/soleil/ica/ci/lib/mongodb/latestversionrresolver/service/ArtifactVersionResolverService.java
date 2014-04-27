package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.ArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.workflow.Workflow;

import java.util.logging.Logger;

/**
 * @author Gregory Boissinot
 */
public class ArtifactVersionResolverService {

    private static final Logger LOGGER = Logger.getLogger(MavenVersionResolverService.class.getName());

    private final ArtifactRepository artifactRepository;

    private final Workflow workflow;

    public ArtifactVersionResolverService(ArtifactRepository artifactRepository) {
        if (artifactRepository == null) {
            throw new NullPointerException("An ArtifactRepository is required.");
        }
        this.artifactRepository = artifactRepository;
        this.workflow = Workflow.DEFAULT_WORKFLOW_STATUS;
    }

    public ArtifactVersionResolverService(ArtifactRepository artifactRepository, Workflow workflow) {

        if (artifactRepository == null) {
            throw new NullPointerException("An ArtifactRepository is required.");
        }
        this.artifactRepository = artifactRepository;

        if (workflow == null) {
            throw new NullPointerException("A Workflow is required.");
        }
        this.workflow = workflow;
    }

    /**
     * @param org
     * @param name
     * @param status
     * @return the latest version for an existing artifact with its status, null otherwise
     */
    public String getLatestVersion(String org, String name, String status) {

        LOGGER.fine(String.format("Resolving the latest version against '%s'.", artifactRepository.getName()));

        String latestVersion = null;
        String curStatus = workflow.getNormalizedStatus(status);
        while (latestVersion == null && curStatus != null) {
            latestVersion = artifactRepository.getLatestVersion(org, name, "binary", curStatus);
            curStatus = workflow.getNextStatusLabel(curStatus);
        }

        return latestVersion;
    }
}
