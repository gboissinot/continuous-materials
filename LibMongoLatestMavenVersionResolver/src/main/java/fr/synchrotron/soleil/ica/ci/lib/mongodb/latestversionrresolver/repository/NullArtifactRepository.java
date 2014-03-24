package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain.MavenInputArtifact;

/**
 * @author Gregory Boissinot
 */
public class NullArtifactRepository implements ArtifactRepository {

    @Override
    public String getName() {
        return "Null Repository (No Repository)";
    }

    @Override
    public String getLatestVersion(MavenInputArtifact mavenInputArtifact) {

        assert (mavenInputArtifact != null) : ("A maven input artifact is required.");
        String version = mavenInputArtifact.getVersion();
        assert (version != null) : "A version for the input Maven artifact is required.";
        assert (version.startsWith(MavenInputArtifact.LATEST_KEYWORD)) : "A version must start with latest. is required.";

        return null;
    }

}
