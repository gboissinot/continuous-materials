package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.DeveloperDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ProjectDocumentLoaderService {

    ProjectDocument loadPomModel(Model model) {

        if (model == null) {
            throw new NullPointerException("A Maven Model is required.");
        }

        ProjectDocument projectDocument = new ProjectDocument();

        projectDocument.setOrg(model.getGroupId());
        projectDocument.setName(model.getArtifactId());
        projectDocument.setDescription(model.getDescription());

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
            final String extractMavenScmUrl = extractMavenScmUrl(connection);
            if (extractMavenScmUrl != null) {
                projectDocument.setScmConnection(extractMavenScmUrl);
            }
        }

        return projectDocument;
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
