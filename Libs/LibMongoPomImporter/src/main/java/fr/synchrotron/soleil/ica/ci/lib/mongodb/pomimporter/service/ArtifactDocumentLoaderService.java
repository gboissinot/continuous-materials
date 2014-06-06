package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDependency;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDependencyExclusion;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.BuildContext;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.BuildTool;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.maven.MavenProjectInfo;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
class ArtifactDocumentLoaderService {

    @SuppressWarnings("unchecked")
    ArtifactDocument populateArtifactDocument(Model model) {

        if (model == null) {
            throw new NullPointerException("A Maven Model is required.");
        }

        StatusVersion statusVersion = extractStatusFromVersion(model.getVersion());
        ArtifactDocument artifactDocument =
                new ArtifactDocument(
                        model.getGroupId(),
                        model.getArtifactId(),
                        statusVersion.version,
                        statusVersion.status);
        artifactDocument.setModules(model.getModules());
        final List<Dependency> dependencies = model.getDependencies();
        if (dependencies != null) {
            List<ArtifactDependency> artifactDependencies = new ArrayList<ArtifactDependency>();
            for (Dependency dependency : dependencies) {
                ArtifactDependency artifactDependency =
                        new ArtifactDependency(
                                dependency.getGroupId(),
                                dependency.getArtifactId(),
                                dependency.getScope());
                List<Exclusion> exclusions = dependency.getExclusions();
                List<ArtifactDependencyExclusion> artifactDependencyExclusions = new ArrayList<ArtifactDependencyExclusion>();
                for (Exclusion exclusion : exclusions) {
                    artifactDependencyExclusions.add(new ArtifactDependencyExclusion(exclusion.getGroupId(), exclusion.getArtifactId()));
                }
                artifactDependency.setExclusions(artifactDependencyExclusions);
                artifactDependencies.add(artifactDependency);
            }
            artifactDocument.setDependencies(artifactDependencies);
        }


        MavenProjectInfo mavenProjectInfo = new MavenProjectInfo();
        final String packaging = model.getPackaging();
        mavenProjectInfo.setPackaging(packaging != null ? packaging : "jar");

        final BuildContext buildContext = new BuildContext();
        final BuildTool buildTool = new BuildTool();
        buildTool.setMaven(mavenProjectInfo);
        buildContext.setBuildTool(buildTool);
        artifactDocument.setBuildContext(buildContext);


        return artifactDocument;
    }

    private StatusVersion extractStatusFromVersion(String version) {
        StatusVersion statusVersion = new StatusVersion();
        if (version.endsWith("-SNAPSHOT")) {
            statusVersion.status = "INTEGRATION";
            statusVersion.version = version.substring(0, version.lastIndexOf("-SNAPSHOT"));
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

    private class StatusVersion {
        String version;
        String status;
    }
}
