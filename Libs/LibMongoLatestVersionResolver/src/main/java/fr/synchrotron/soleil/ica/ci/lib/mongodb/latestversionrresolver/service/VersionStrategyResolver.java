package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenInputArtifact;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenOutputArtifact;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.ArtifactRepository;

import java.util.logging.Logger;

/**
 * @author Gregory Boissinot
 */
public class VersionStrategyResolver {

    private static final Logger LOGGER = Logger.getLogger(VersionStrategyResolver.class.getName());

    private final ArtifactRepository artifactRepository;

    public VersionStrategyResolver(ArtifactRepository artifactRepository) {
        if (artifactRepository == null) {
            throw new NullPointerException("A given artifact repository is required.");
        }
        this.artifactRepository = artifactRepository;
    }

    /**
     * Resolves a Maven input artifacts against an artifact repository
     *
     * @param mavenInputArtifact the maven input artifact with its GAV information
     * @return the maven output artifact with the resolved version
     */
    public MavenOutputArtifact resolveMavenArtifact(MavenInputArtifact mavenInputArtifact) {

        if (mavenInputArtifact == null) {
            throw new NullPointerException("A given mavenInputArtifact is required.");
        }

        String resolvedVersion = resolveMavenVersion(mavenInputArtifact);
        LOGGER.info(String.format("The resolved version is '%s'.", resolvedVersion));
        return new MavenOutputArtifact(
                mavenInputArtifact.getGroupId(),
                mavenInputArtifact.getArtifactId(),
                resolvedVersion);
    }

    /**
     * Resolves a Maven input artifacts against an artifact repository
     *
     * @param mavenInputArtifact the maven input artifact with its GAV information
     * @return the resolved version
     */
    private String resolveMavenVersion(MavenInputArtifact mavenInputArtifact) {

        String inputVersion = mavenInputArtifact.getVersion();

        if (inputVersion.startsWith(MavenInputArtifact.LATEST_KEYWORD)) {
            LOGGER.info(String.format("Resolving the latest version against '%s'.", artifactRepository.getName()));
            String latestVersion = artifactRepository.getLatestVersion(mavenInputArtifact.getGroupId(),
                    mavenInputArtifact.getArtifactId(),
                    "binary",
                    mavenInputArtifact.getVersion());


            if (latestVersion == null) {
                return inputVersion;
            }


            return latestVersion;
        }



        return inputVersion;
    }

}
