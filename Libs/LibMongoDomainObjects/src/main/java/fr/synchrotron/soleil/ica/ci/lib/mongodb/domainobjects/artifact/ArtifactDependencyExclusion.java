package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact;

/**
 * Created by ABEILLE on 25/04/2014.
 */
public class ArtifactDependencyExclusion {
    /**
     * The artifact ID of the project to exclude.
     */
    private String artifactId;

    /**
     * The group ID of the project to exclude.
     */
    private String groupId;

    public ArtifactDependencyExclusion() {
    }

    public ArtifactDependencyExclusion(String groupId, String artifactId ) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    /**
     * Get the artifact ID of the project to exclude.
     *
     * @return String
     */
    public String getArtifactId() {
        return this.artifactId;
    }

    /**
     * Get the group ID of the project to exclude.
     *
     * @return String
     */
    public String getGroupId() {
        return this.groupId;
    }

    /**
     * Set the artifact ID of the project to exclude.
     *
     * @param artifactId
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * Set the group ID of the project to exclude.
     *
     * @param groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
