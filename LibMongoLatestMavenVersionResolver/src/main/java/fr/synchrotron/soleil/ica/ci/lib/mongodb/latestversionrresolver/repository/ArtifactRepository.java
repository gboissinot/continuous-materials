package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenInputArtifact;

/**
 * @author Gregory Boissinot
 */
public interface ArtifactRepository {

    /**
     * Gets the logical name of the artifact metadata repository
     * Mainly used for logging.
     *
     * @return the artifact repository name
     */
    public String getName();

    /**
     * Gets the latest version for an input Maven artifact
     *
     * @param mavenInputArtifact the maven input artifact with GroupId, ArtifactId and status elements
     * @return the latest version if one can be computed from the store system; null otherwise
     */
    public String getLatestVersion(MavenInputArtifact mavenInputArtifact);

}
