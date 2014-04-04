package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.DeveloperDocument;

import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ProjectDocument {

    private String org;

    private String name;

    private List<DeveloperDocument> developers;

    private String description;

    private String scmConnection;

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

    public List<DeveloperDocument> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<DeveloperDocument> developers) {
        this.developers = developers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScmConnection() {
        return scmConnection;
    }

    public void setScmConnection(String scmConnection) {
        this.scmConnection = scmConnection;
    }
}
