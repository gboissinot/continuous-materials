package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project;

/**
 * @author Gregory Boissinot
 */
public class BaseProjectDocument {

    public static final String MONGO_PROJECTS_COLLECTION_NAME = "projects";

    private String org;

    private String name;

    public BaseProjectDocument() {
    }

    public BaseProjectDocument(String org, String name) {
        this.org = org;
        this.name = name;
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
}
