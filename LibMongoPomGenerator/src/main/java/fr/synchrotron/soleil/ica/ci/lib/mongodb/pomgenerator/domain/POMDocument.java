package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomgenerator.domain;


import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ProjectDocument;


/**
 * @author Gregory Boissinot
 */
public class POMDocument {

    private ArtifactDocument aritfactDocument;

    private ProjectDocument projectDocument;

    public POMDocument() {
    }

    public ArtifactDocument getAritfactDocument() {
        return aritfactDocument;
    }

    public ProjectDocument getProjectDocument() {
        return projectDocument;
    }

    public void setAritfactDocument(ArtifactDocument aritfactDocument) {
        this.aritfactDocument = aritfactDocument;
    }

    public void setProjectDocument(ProjectDocument projectDocument) {
        this.projectDocument = projectDocument;
    }
}
