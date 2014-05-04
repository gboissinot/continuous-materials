package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDependency;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDependencyExclusion;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.BuildContext;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.BuildTool;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.DeveloperDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.maven.MavenProjectInfo;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.LicenseDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.OrganisationDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class POMExportService {

    private POMDocumentRepository pomDocumentRepository;

    public POMExportService(POMDocumentRepository pomDocumentRepository) {
        if (pomDocumentRepository == null) {
            throw new NullPointerException("A pomDocumentRepository is required.");
        }
        this.pomDocumentRepository = pomDocumentRepository;
    }

    public void exportPomFile(Writer writer, ArtifactDocumentKey artifactDocumentKey) throws POMExporterException {

        if (writer == null) {
            throw new NullPointerException("An writer element is required.");
        }

        if (artifactDocumentKey == null) {
            throw new NullPointerException("A key artifact is required.");
        }

        if (!artifactDocumentKey.isValid()) {
            throw new NullPointerException("All key artifact document elements must be set.");
        }

        final MavenXpp3Writer mavenXpp3Writer = new MavenXpp3Writer();
        Model pomModel = getMavenModel(
                artifactDocumentKey.getOrg(),
                artifactDocumentKey.getName(),
                artifactDocumentKey.getVersion(),
                artifactDocumentKey.getStatus());
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

        final ArtifactDocument artifactDocument = pomDocument.getAritfactDocument();
        pomModel.setGroupId(artifactDocument.getKey().getOrg());
        pomModel.setArtifactId(artifactDocument.getKey().getName());
        pomModel.setVersion(artifactDocument.getKey().getVersion() + "." + artifactDocument.getKey().getStatus());
        pomModel.setModules(artifactDocument.getModules());
        final List<ArtifactDependency> dependencies = artifactDocument.getDependencies();
        if (dependencies != null) {
            for (ArtifactDependency artifactDependency : dependencies) {
                Dependency dependency = new Dependency();
                dependency.setGroupId(artifactDependency.getOrg());
                dependency.setArtifactId(artifactDependency.getName());
                //TODO Check null version
                dependency.setVersion(artifactDependency.getVersion());
                dependency.setScope(artifactDependency.getScope());
                pomModel.addDependency(dependency);
                List<ArtifactDependencyExclusion> artifactDependencyExclusions = artifactDependency.getExclusions();
                List<Exclusion> exclusions = new ArrayList<Exclusion>();
                for (ArtifactDependencyExclusion artifactDependencyExclusion : artifactDependencyExclusions) {
                    Exclusion ex = new Exclusion();
                    ex.setGroupId(artifactDependencyExclusion.getGroupId());
                    ex.setArtifactId(artifactDependencyExclusion.getArtifactId());
                    exclusions.add(ex);
                }
                dependency.setExclusions(exclusions);
            }
        }

        //Extract Maven specifities
        final BuildContext buildContext = artifactDocument.getBuildContext();
        if (buildContext != null) {
            final BuildTool buildTool = buildContext.getBuildTool();
            if (buildTool != null) {
                final MavenProjectInfo mavenProjectInfo = buildTool.getMaven();
                if (mavenProjectInfo != null) {
                    pomModel.setPackaging(mavenProjectInfo.getPackaging());
                }
            }
        }

        //-- Populate from ProjectDocument
        final ProjectDocument projectDocument = pomDocument.getProjectDocument();
        pomModel.setDescription(projectDocument.getDescription());
        pomModel.setInceptionYear(projectDocument.getInceptionYear());

        OrganisationDocument organisationDocument = projectDocument.getOrganisation();
        if (organisationDocument != null) {
            Organization organization = new Organization();
            organization.setUrl(organisationDocument.getUrl());
            organization.setName(organisationDocument.getName());
            pomModel.setOrganization(organization);
        }


        final List<DeveloperDocument> developers = projectDocument.getDevelopers();
        List<Developer> developersMaven = new ArrayList<Developer>();
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
                developersMaven.add(developer);
            }
        }
        pomModel.setDevelopers(developersMaven);

        // Licence
        List<LicenseDocument> licenses = projectDocument.getLicences();
        List<License> licencesMaven = new ArrayList<License>();
        if (licenses != null) {
            for (LicenseDocument license : licenses) {
                License licenseMaven = new License();
                licenseMaven.setName(license.getName());
                licenseMaven.setUrl(license.getUrl());
                licenseMaven.setDistribution(license.getDistribution());
                licenseMaven.setComments(license.getComments());
                licencesMaven.add(licenseMaven);
            }
        }
        pomModel.setLicenses(licencesMaven);

        final String scmConnection = projectDocument.getScmConnection();
        if (scmConnection != null) {
            final Scm scm = new Scm();
            scm.setConnection(scmConnection);
            pomModel.setScm(scm);
        }

        return pomModel;
    }
}
