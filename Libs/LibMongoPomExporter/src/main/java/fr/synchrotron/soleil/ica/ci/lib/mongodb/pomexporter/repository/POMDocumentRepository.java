package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.repository;

import com.google.gson.Gson;
import com.mongodb.DB;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.BaseArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.BaseProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
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


    public POMDocument loadPOMDocument(String org, String name, String version, String status) {

        POMDocument pomDocumentResult = new POMDocument();
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);

        pomDocumentResult.setAritfactDocument(loadArtifactDocument(jongo, org, name, version, status));
        pomDocumentResult.setProjectDocument(loadProjectDocument(jongo, org, name));

        return pomDocumentResult;

    }

    private ArtifactDocument loadArtifactDocument(Jongo jongo, String org, String name, String version, String status) {

        MongoCollection artifacts = jongo.getCollection(BaseArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);

        BaseArtifactDocument queryObject = new BaseArtifactDocument(org, name, version, status);
        Gson gson = new Gson();
        final Iterable<ArtifactDocument> artifactDocuments = artifacts.find(gson.toJson(queryObject)).as(ArtifactDocument.class);
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

        MongoCollection projects = jongo.getCollection(BaseProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        BaseProjectDocument queryObject = new BaseProjectDocument(org, name);

        Gson gson = new Gson();
        final Iterable<ProjectDocument> projectDocuments = projects.find(gson.toJson(queryObject)).as(ProjectDocument.class);
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
