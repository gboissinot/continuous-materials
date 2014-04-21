package fr.synchrotron.soleil.ica.ci.lib.mongodb.repository;

import com.mongodb.WriteConcern;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.exception.DuplicateElementException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.exception.NoSuchElementException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.jongo.MongoCollection;

import java.util.Iterator;

/**
 * @author Gregory Boissinot
 */
public class ArtifactRepository extends AbstractRepository {

    public ArtifactRepository(MongoDBDataSource mongoDBDataSource) {
        super(mongoDBDataSource);
    }

    public ArtifactDocument findArtifactDocument(String org, String name, String version, String status) {

        MongoCollection artifacts = jongo.getCollection(ArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);

        ArtifactDocumentKey queryObject = new ArtifactDocumentKey(org, name, version, status);
        String query = getStringQuery(queryObject);

        final Iterable<ArtifactDocument> artifactDocuments = artifacts.find(query).as(ArtifactDocument.class);
        final Iterator<ArtifactDocument> artifactDocumentIterator = artifactDocuments.iterator();
        if (!artifactDocumentIterator.hasNext()) {
            throw new NoSuchElementException("At least one Artifact document must match criteria.");
        }

        final ArtifactDocument artifactDocument = artifactDocumentIterator.next();
        if (artifactDocumentIterator.hasNext()) {
            throw new DuplicateElementException("Only one Artifact document must be returned.");
        }

        return artifactDocument;
    }

    public boolean isArtifactDocumentAlreadyExists(ArtifactDocumentKey artifactDocumentKey) {
        MongoCollection artifacts = jongo.getCollection(ArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        return artifacts.count(getStringQuery(artifactDocumentKey)) != 0;
    }

    public void updateArtifactDocument(ArtifactDocument artifactDocument) {
        MongoCollection artifacts = jongo.getCollection(ArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        artifacts.withWriteConcern(WriteConcern.SAFE);
        artifacts.update(getStringQuery(artifactDocument.getKey())).with(artifactDocument);
    }

    public void insertArtifactDocument(ArtifactDocument artifactDocument) {
        MongoCollection artifacts = jongo.getCollection(ArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        artifacts.insert(artifactDocument);
    }

}
