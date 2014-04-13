package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.domain;

/**
 * @author Gregory Boissinot
 */
public class MavenInputArtifact {

    public static final String LATEST_KEYWORD = "latest.";

    private final String groupId;

    private final String artifactId;

    private final String version;

    public MavenInputArtifact(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

}
