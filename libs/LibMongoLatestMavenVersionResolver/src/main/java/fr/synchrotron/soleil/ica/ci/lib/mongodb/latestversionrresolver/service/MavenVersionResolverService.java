package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;


import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenInputArtifact;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenOutputArtifact;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.exception.MavenVersionResolverException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.ArtifactRepository;

import java.util.logging.Logger;

/**
 * @author Gregory Boissinot
 */
public class MavenVersionResolverService {

    public static final String LATEST_RELEASE_VERSION_VALUE = "latest.release";

    private static final Logger LOGGER = Logger.getLogger(MavenVersionResolverService.class.getName());

    private final ArtifactRepository artifactRepository;

    public MavenVersionResolverService(ArtifactRepository artifactRepository) {
        if (artifactRepository == null) {
            throw new NullPointerException("An ArtifactRepository is required.");
        }
        this.artifactRepository = artifactRepository;
    }

    /**
     * Gets the latest version from an entry Maven artifact
     *
     * @param groupId    the specified Maven groupId
     * @param artifactId the specified Maven artifactId
     * @return the resolved version
     */
    public String getLatestArtifact(String groupId, String artifactId) {

        if (groupId == null) {
            throw new NullPointerException("A groupId is required.");
        }

        if (artifactId == null) {
            throw new NullPointerException("A artifactId is required.");
        }

        MavenOutputArtifact mavenOutputArtifact =
                resolveArtifact(new MavenInputArtifact(groupId, artifactId, LATEST_RELEASE_VERSION_VALUE));

        return mavenOutputArtifact.getVersion();
    }

    /**
     * Resolves the version from an input artifact
     *
     * @param mavenInputArtifact the maven input artifact
     * @return the maven output artifact
     */
    public MavenOutputArtifact resolveArtifact(MavenInputArtifact mavenInputArtifact) {

        if (mavenInputArtifact == null) {
            throw new NullPointerException("An input artifact is required.");
        }

        LOGGER.info(String.format("Resolving version from Maven Artifact '(%s,%s,%s)'.",
                mavenInputArtifact.getGroupId(),
                mavenInputArtifact.getArtifactId(),
                mavenInputArtifact.getVersion()));

        try {
            VersionStrategyResolver versionStrategyResolver
                    = new VersionStrategyResolver(artifactRepository);

            return versionStrategyResolver.resolveMavenArtifact(mavenInputArtifact);

        } catch (Throwable e) {
            throw new MavenVersionResolverException(e);
        }
    }

}
