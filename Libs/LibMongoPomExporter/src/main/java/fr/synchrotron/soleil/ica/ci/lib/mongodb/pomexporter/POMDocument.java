package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;

/**
 * @author Gregory Boissinot
 */
class POMDocument {

    private ArtifactDocument aritfactDocument;

    private ProjectDocument projectDocument;

    public POMDocument() {
    }

    public ArtifactDocument getAritfactDocument() {
        return aritfactDocument;
    }

    public void setAritfactDocument(ArtifactDocument aritfactDocument) {
        this.aritfactDocument = aritfactDocument;
    }

    public ProjectDocument getProjectDocument() {
        return projectDocument;
    }

    public void setProjectDocument(ProjectDocument projectDocument) {
        this.projectDocument = projectDocument;
    }
}
