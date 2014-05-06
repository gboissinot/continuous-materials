package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.legacyproxy.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.ArtifactRepository;
import org.apache.maven.model.*;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Gregory Boissinot
 */
public class PomModelBuilder {

    private ArtifactRepository artifactRepository;

    public PomModelBuilder(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    public Model getModelWithResolvedParent(String originalPomContent) {

        PomModelRetriever modelRetriever = new PomModelRetriever();
        StringReader pomReader = new StringReader(originalPomContent);
        final Model model = modelRetriever.getModel(pomReader);
        pomReader.close();


        final Parent parent = model.getParent();
        if (parent != null && "RELEASE".equals(parent.getVersion())) {
            MavenParentVersionResolver versionResolver = new MavenParentVersionResolver(artifactRepository);
            final String resolvedParentVersion = versionResolver.resolveParentVersion(parent.getGroupId(), parent.getArtifactId());
            parent.setVersion(resolvedParentVersion);
        }

        List profiles = model.getProfiles();
        if (profiles != null) {
            for (Object curProfile : profiles) {
                Profile profile = (Profile) curProfile;
                if (profile != null) {
                    String profilName = profile.getId();
                    if ("release".equals(profilName) || ("stable".equals(profilName))) {
                        Properties properties = profile.getProperties();
                        if (properties != null) {
                            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                                model.addProperty((String) entry.getKey(), (String) entry.getValue());
                            }
                        }
                    }
                }
            }
        }

        DependencyManagement dependencyManagement = model.getDependencyManagement();
        if (dependencyManagement != null) {
            List dependencies = dependencyManagement.getDependencies();
            if (dependencies != null) {
                for (int k = 0; k < dependencies.size(); k++) {
                    Dependency dependency = (Dependency) dependencies.get(k);
                    String artifactId = dependency.getArtifactId();
                    if ("C-CPP-Devices".equals(artifactId)) {
                        dependencies.remove(k);
                    }
                }
            }
        }


        return model;
    }
}
