package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomgenerator.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ArtifactDependency;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.DeveloperDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomgenerator.domain.POMDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomgenerator.repository.POMDocumentRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;

import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class POMManagementService {

    private POMDocumentRepository pomDocumentRepository;

    public POMManagementService(POMDocumentRepository pomDocumentRepository) {
        this.pomDocumentRepository = pomDocumentRepository;
    }

    public Model getMavenModel(String org, String name, String status, String version) {


        Model pomModel = new Model();

        final POMDocument pomDocument = pomDocumentRepository.loadPOMDocument(org, name, status, version);

        final ArtifactDocument aritfactDocument = pomDocument.getAritfactDocument();
        pomModel.setGroupId(aritfactDocument.getOrganisation());
        pomModel.setArtifactId(aritfactDocument.getName());
        pomModel.setVersion(aritfactDocument.getVersion() + "." + aritfactDocument.getStatus());

        final List<ArtifactDependency> dependencies = aritfactDocument.getDependencies();
        if (dependencies != null) {
            for (ArtifactDependency artifactDependency : dependencies) {
                Dependency dependency = new Dependency();
                dependency.setGroupId(artifactDependency.getOrg());
                dependency.setArtifactId(artifactDependency.getName());
                //TODO Check null version
                dependency.setVersion(artifactDependency.getVersion());
                dependency.setScope(artifactDependency.getScope());
                pomModel.addDependency(dependency);
            }
        }


        final ProjectDocument projectDocument = pomDocument.getProjectDocument();
        pomModel.setDescription(projectDocument.getDescription());

        final List<DeveloperDocument> developers = projectDocument.getDevelopers();
        if (developers != null) {
            for (DeveloperDocument developerDocument : developers) {
                Developer developer = new Developer();
                developer.setId(developerDocument.getId());
                developer.setName(developerDocument.getName());
                developer.setEmail(developerDocument.getEmail());
                developer.setUrl(developerDocument.getUrl());
                developer.setRoles(developerDocument.getRoles());
                developer.setOrganization(developerDocument.getOrganization());
                developer.setOrganizationUrl(developerDocument.getOrganizationUrl());
                developer.setTimezone(developerDocument.getTimezone());
                //TODO Missing properties
            }
        }

        final String scmConnection = projectDocument.getScmConnection();
        if (scmConnection != null) {
            final Scm scm = new Scm();
            scm.setConnection(scmConnection);
            pomModel.setScm(scm);
        }

        return pomModel;
    }
}
