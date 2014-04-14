package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.DeveloperDocument;

import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ProjectDocument {

    public static final String MONGO_PROJECTS_COLLECTION_NAME = "projects";

    private String org;

    private String name;

    public ProjectDocument() {
    }

    public ProjectDocument(String org, String name) {
        this.org = org;
        this.name = name;
    }

    public ProjectDocumentKey getKey() {
        return new ProjectDocumentKey(org, name);
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

    private List<DeveloperDocument> developers;

    private String description;

    private String scmConnection;

    private String language;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
