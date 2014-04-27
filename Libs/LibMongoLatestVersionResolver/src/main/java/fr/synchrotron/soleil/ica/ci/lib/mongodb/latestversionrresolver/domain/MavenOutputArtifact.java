package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain;

/**
 * @author Gregory Boissinot
 */
public class MavenOutputArtifact extends MavenInputArtifact {

    public MavenOutputArtifact(String groupId, String artifactId, String version) {
        super(groupId, artifactId, version);
    }
}
