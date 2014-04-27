package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service;


import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenInputArtifact;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenOutputArtifact;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.exception.MavenVersionResolverException;

import java.util.logging.Logger;

/**
 * @author Gregory Boissinot
 */
public class MavenVersionResolverService {

    private static final Logger LOGGER = Logger.getLogger(MavenVersionResolverService.class.getName());

    private ArtifactVersionResolverService artifactVersionResolverService;

    public MavenVersionResolverService(ArtifactVersionResolverService artifactVersionResolverService) {
        this.artifactVersionResolverService = artifactVersionResolverService;
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
                resolveArtifact(new MavenInputArtifact(groupId, artifactId, MavenInputArtifact.LATEST_KEYWORD));

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

        try {
            LOGGER.info(String.format("Resolving version from Maven Artifact '%s'", mavenInputArtifact.printMavenArtifact()));

            String inputVersion = mavenInputArtifact.getVersion();

            if (inputVersion.equalsIgnoreCase(MavenInputArtifact.LATEST_KEYWORD)) {
                return buildMavenOutputArtifact(
                        mavenInputArtifact,
                        artifactVersionResolverService.getLatestVersion(
                                mavenInputArtifact.getGroupId(),
                                mavenInputArtifact.getArtifactId(),
                                MavenInputArtifact.NO_STATUS)
                );
            }

            if (inputVersion.startsWith(MavenInputArtifact.LATEST_STATUS_PATTERN)) {
                String requestedStatus = inputVersion.substring(MavenInputArtifact.LATEST_STATUS_PATTERN.length());
                return buildMavenOutputArtifact(
                        mavenInputArtifact,
                        artifactVersionResolverService.getLatestVersion(
                                mavenInputArtifact.getGroupId(),
                                mavenInputArtifact.getArtifactId(),
                                requestedStatus)
                );
            }

            return buildMavenOutputArtifact(mavenInputArtifact, inputVersion);

        } catch (Throwable e) {
            throw new MavenVersionResolverException(e);
        }
    }

    private MavenOutputArtifact buildMavenOutputArtifact(MavenInputArtifact mavenInputArtifact, String resolvedVersion) {
        if (resolvedVersion != null) {
            return new MavenOutputArtifact(
                    mavenInputArtifact.getGroupId(),
                    mavenInputArtifact.getArtifactId(),
                    resolvedVersion);
        } else {
            return new MavenOutputArtifact(
                    mavenInputArtifact.getGroupId(),
                    mavenInputArtifact.getArtifactId(),
                    mavenInputArtifact.getVersion());
        }
    }


}
