package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.mongodb.integration;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import org.springframework.integration.annotation.Filter;

/**
 * @author Gregory Boissinot
 */
public class BinaryArtifactFilter {

    @Filter
    @SuppressWarnings("unused")
    public boolean filteringBinaryArtifacts(ArtifactDocument artifactObj) {
        return ("binary".equals(artifactObj.getType()));
    }
}
