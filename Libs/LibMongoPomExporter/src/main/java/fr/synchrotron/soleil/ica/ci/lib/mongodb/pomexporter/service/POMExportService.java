package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDependency;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.DeveloperDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.BuildContext;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.BuildTool;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.maven.MavenProjectInfo;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.domain.POMDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.exception.POMExporterException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.repository.POMDocumentRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class POMExportService {

    private POMDocumentRepository pomDocumentRepository;

    public POMExportService(POMDocumentRepository pomDocumentRepository) {
        this.pomDocumentRepository = pomDocumentRepository;
    }

    public void exportPomFile(Writer writer, String org, String name, String version, String status) throws POMExporterException {

        final MavenXpp3Writer mavenXpp3Writer = new MavenXpp3Writer();
        Model pomModel = getMavenModel(org, name, version, status);
        try {
            mavenXpp3Writer.write(writer, pomModel);
        } catch (IOException ioe) {
            throw new POMExporterException(ioe);
        }
    }

    private Model getMavenModel(String org, String name, String version, String status) {


        Model pomModel = new Model();

        final POMDocument pomDocument = pomDocumentRepository.loadPOMDocument(org, name, version, status);

        //-- Populate form ArtifactDocument

        final ArtifactDocument aritfactDocument = pomDocument.getAritfactDocument();
        pomModel.setGroupId(aritfactDocument.getKey().getOrg());
        pomModel.setArtifactId(aritfactDocument.getKey().getName());
        pomModel.setVersion(aritfactDocument.getKey().getVersion() + "." + aritfactDocument.getKey().getStatus());

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

        //Extract Maven specifities
        final BuildContext buildContext = aritfactDocument.getBuildContext();
        if (buildContext != null) {
            final BuildTool buildTool = buildContext.getBuildTool();
            if (buildTool != null) {
                final MavenProjectInfo mavenProjectInfo = buildTool.getMaven();
                if (mavenProjectInfo != null) {
                    pomModel.setPackaging(mavenProjectInfo.getPackaging());
                }
            }
        }

        //-- Populate form ProjectDocument
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
