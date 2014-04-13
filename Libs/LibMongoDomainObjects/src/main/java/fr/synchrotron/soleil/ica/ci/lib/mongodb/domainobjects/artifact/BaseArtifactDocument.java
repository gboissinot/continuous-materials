package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact;

/**
 * @author Gregory Boissinot
 */
public class BaseArtifactDocument {

    public static final String MONGO_ARTIFACTS_COLLECTION_NAME = "artifacts";

    private String org;

    private String name;

    private String version;

    private String status;

    public BaseArtifactDocument() {
    }

    public BaseArtifactDocument(String org, String name, String version, String status) {
        this.org = org;
        this.name = name;
        this.version = version;
        this.status = status;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
