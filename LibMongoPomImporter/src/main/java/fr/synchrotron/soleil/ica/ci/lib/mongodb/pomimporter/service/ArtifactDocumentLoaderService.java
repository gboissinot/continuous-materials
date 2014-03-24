package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ArtifactDependency;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ArtifactDocument;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ArtifactDocumentLoaderService {

    ArtifactDocument loadPomModel(Model model) {

        if (model == null) {
            throw new NullPointerException("A Maven Model is required.");
        }

        ArtifactDocument artifactDocument = new ArtifactDocument();

        artifactDocument.setOrganisation(model.getGroupId());
        artifactDocument.setName(model.getName());
        artifactDocument.setVersion(model.getVersion());
        artifactDocument.setStatus("RELEASE");

        final List dependencies = model.getDependencies();
        if (dependencies != null) {
            List<ArtifactDependency> artifactDependencies = new ArrayList<ArtifactDependency>();
            for (Object dependencyObject : dependencies) {
                Dependency dependency = (Dependency) dependencyObject;
                //We can't determine here the right dependency due to it require a runtime
                // maven context
                ArtifactDependency artifactDependency =
                        new ArtifactDependency(
                                dependency.getGroupId(),
                                dependency.getArtifactId(),
                                dependency.getScope());

                artifactDependencies.add(artifactDependency);
            }
            artifactDocument.setDependencies(artifactDependencies);
        }


        return artifactDocument;
    }
}
