package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.DeveloperDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.LicenseDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.OrganisationDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.dictionary.Dictionary;
import org.apache.maven.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
class ProjectDocumentLoaderService {

    private Dictionary dictionary;

    ProjectDocumentLoaderService(Dictionary dictionary) {
        if (dictionary == null) {
            throw new NullPointerException("You must provide an input dictionary.");
        }
        this.dictionary = dictionary;
    }

    @SuppressWarnings("unchecked")
    ProjectDocument populateProjectDocument(Model model) {

        if (model == null) {
            throw new NullPointerException("A Maven Model is required.");
        }

        ProjectDocument projectDocument = new ProjectDocument(model.getGroupId(), model.getArtifactId());
        projectDocument.setDescription(model.getDescription());
        projectDocument.setInceptionYear(model.getInceptionYear());
        Organization org = model.getOrganization();
        if (org != null) {
            projectDocument.setOrganisation(new OrganisationDocument(org.getName(), org.getUrl()));
        }

        List<License> licences = model.getLicenses();
        List<LicenseDocument> licenseDocuments = new ArrayList<LicenseDocument>();
        for (License licence : licences) {
            licenseDocuments.add(new LicenseDocument(licence.getName(), licence.getUrl(), licence.getDistribution(), licence.getComments()));
        }
        projectDocument.setLicences(licenseDocuments);

        List developers = model.getDevelopers();
        List<DeveloperDocument> developerDocuments = new ArrayList<DeveloperDocument>();
        for (Object developerObject : developers) {
            Developer developer = (Developer) developerObject;
            DeveloperDocument developerDocument = new DeveloperDocument();
            developerDocument.setId(developer.getId());
            developerDocument.setName(developer.getName());
            developerDocument.setEmail(developer.getEmail());
            developerDocument.setOrganization(developer.getOrganization());
            developerDocument.setRoles(developer.getRoles());
            developerDocument.setTimezone(developer.getTimezone());
            developerDocument.setUrl(developer.getUrl());
            developerDocuments.add(developerDocument);
        }
        projectDocument.setDevelopers(developerDocuments);

        final Scm scm = model.getScm();
        if (scm != null) {
            final String connection = scm.getConnection();
            final String extractMavenScmUrl = extractMavenScmUrl(resolve(connection));
            if (extractMavenScmUrl != null) {
                projectDocument.setScmConnection(resolve(extractMavenScmUrl));
            }
        }

        return projectDocument;
    }

    private String resolve(String value) {
        return dictionary.resolve(value);
    }

    private String extractMavenScmUrl(String mavenScmUrl) {
        final String[] scmParts = mavenScmUrl.split(":");
        if (scmParts.length >= 3) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 2; i < scmParts.length; i++) {
                stringBuilder.append(":");
                stringBuilder.append(scmParts[i]);
            }
            stringBuilder.delete(0, 1);
            return stringBuilder.toString();
        }
        return null;
    }
}
