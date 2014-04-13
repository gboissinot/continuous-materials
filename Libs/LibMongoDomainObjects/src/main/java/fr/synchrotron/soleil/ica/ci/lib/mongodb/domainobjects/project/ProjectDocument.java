package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.DeveloperDocument;

import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ProjectDocument extends BaseProjectDocument {

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
