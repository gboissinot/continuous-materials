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


    private class StatusVersion {
        String version;
        String status;

        private StatusVersion() {
        }

        public String getVersion() {
            return version;
        }

        public String getStatus() {
            return status;
        }
    }

    ArtifactDocument loadPomModel(Model model) {

        if (model == null) {
            throw new NullPointerException("A Maven Model is required.");
        }

        ArtifactDocument artifactDocument = new ArtifactDocument();

        artifactDocument.setOrganisation(model.getGroupId());
        artifactDocument.setName(model.getName());
        StatusVersion statusVersion = extractStatusFromVersion(model.getVersion());
        artifactDocument.setVersion(statusVersion.version);
        artifactDocument.setStatus(statusVersion.status);

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

    private StatusVersion extractStatusFromVersion(String version) {
        StatusVersion statusVersion = new StatusVersion();
        if (version.endsWith("-SNAPSHOT")) {
            statusVersion.status = "INTEGRATION";
            statusVersion.version = version;
            return statusVersion;
        }

        if (version.endsWith(".RELEASE")) {
            statusVersion.status = "RELEASE";
            statusVersion.version = version.substring(0, version.lastIndexOf(".RELEASE"));
            return statusVersion;
        }

        statusVersion.status = "RELEASE";
        statusVersion.version = version;
        return statusVersion;
    }
}
