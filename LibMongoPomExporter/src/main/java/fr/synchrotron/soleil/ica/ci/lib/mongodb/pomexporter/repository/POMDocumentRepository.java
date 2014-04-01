package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.repository;

import com.google.gson.Gson;
import com.mongodb.DB;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.domain.POMDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.exception.POMExporterException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.util.Iterator;

/**
 * @author Gregory Boissinot
 */
public class POMDocumentRepository {

    private MongoDBDataSource mongoDBDataSource;

    public POMDocumentRepository(MongoDBDataSource mongoDBDataSource) {
        this.mongoDBDataSource = mongoDBDataSource;
    }


    public POMDocument loadPOMDocument(String org, String name, String status, String version) {

        POMDocument pomDocumentResult = new POMDocument();
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);

        pomDocumentResult.setAritfactDocument(loadArtifactDocument(jongo, org, name, status, version));
        pomDocumentResult.setProjectDocument(loadProjectDocument(jongo, org, name));

        return pomDocumentResult;

    }

    private ArtifactDocument loadArtifactDocument(Jongo jongo, String org, String name, String status, String version) {

        MongoCollection artifacts = jongo.getCollection("artifacts");
        ArtifactDocument artifactDocumentQuery = new ArtifactDocument();
        artifactDocumentQuery.setOrganisation(org);
        artifactDocumentQuery.setName(name);
        artifactDocumentQuery.setStatus(status);
        artifactDocumentQuery.setVersion(version);

        Gson gson = new Gson();
        final Iterable<ArtifactDocument> artifactDocuments = artifacts.find(gson.toJson(artifactDocumentQuery)).as(ArtifactDocument.class);
        final Iterator<ArtifactDocument> artifactDocumentIterator = artifactDocuments.iterator();
        if (!artifactDocumentIterator.hasNext()) {
            throw new POMExporterException("At least one Artifact document must match criteria.");
        }

        final ArtifactDocument artifactDocument = artifactDocumentIterator.next();
        if (artifactDocumentIterator.hasNext()) {
            throw new POMExporterException("Only one Artifact document must be returned.");
        }

        return artifactDocument;

    }

    private ProjectDocument loadProjectDocument(Jongo jongo, String org, String name) {

        MongoCollection projects = jongo.getCollection("projects");
        ProjectDocument projectDocumentQuery = new ProjectDocument();
        projectDocumentQuery.setOrg(org);
        projectDocumentQuery.setName(name);

        Gson gson = new Gson();
        final Iterable<ProjectDocument> projectDocuments = projects.find(gson.toJson(projectDocumentQuery)).as(ProjectDocument.class);
        final Iterator<ProjectDocument> projectDocumentIterator = projectDocuments.iterator();
        if (!projectDocumentIterator.hasNext()) {
            throw new POMExporterException("One Project document must match criteria.");
        }

        final ProjectDocument projectDocument = projectDocumentIterator.next();
        if (projectDocumentIterator.hasNext()) {
            throw new POMExporterException("Only one Project document must be returned.");
        }

        return projectDocument;
    }


}
