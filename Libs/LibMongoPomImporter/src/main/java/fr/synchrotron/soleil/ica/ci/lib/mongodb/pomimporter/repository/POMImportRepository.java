package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.repository;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.WriteConcern;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.BaseArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.BaseProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

/**
 * @author Gregory Boissinot
 */
public class POMImportRepository {

    private MongoDBDataSource mongoDBDataSource;

    public POMImportRepository(MongoDBDataSource mongoDBDataSource) {
        this.mongoDBDataSource = mongoDBDataSource;
    }

    /*
     *   Artifact Document
     */

    public boolean isArtifactDocumentAlreadyExists(ArtifactDocument artifactDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection artifacts = jongo.getCollection(BaseArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        BaseArtifactDocument queryObject =
                new BaseArtifactDocument(artifactDocument.getOrg(),
                        artifactDocument.getName(),
                        artifactDocument.getStatus(),
                        artifactDocument.getVersion());
        Gson gson = new Gson();
        return artifacts.count(gson.toJson(queryObject)) != 0;
    }

    public void updateArtifactDocument(ArtifactDocument artifactDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection artifacts = jongo.getCollection(BaseArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        artifacts.withWriteConcern(WriteConcern.SAFE);
        BaseArtifactDocument queryObject =
                new BaseArtifactDocument(artifactDocument.getOrg(),
                        artifactDocument.getName(),
                        artifactDocument.getStatus(),
                        artifactDocument.getVersion());
        Gson gson = new Gson();
        artifacts.update(gson.toJson(queryObject)).with(artifactDocument);
    }

    public void insertArtifactDocument(ArtifactDocument artifactDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection artifacts = jongo.getCollection(BaseArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        artifacts.insert(artifactDocument);
    }

    /*
     *   Project Document
     */

    public boolean isProjectDocumentAlreadyExists(ProjectDocument projectDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection projects = jongo.getCollection(BaseProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        BaseProjectDocument queryObject = new BaseProjectDocument(projectDocument.getOrg(), projectDocument.getName());
        Gson gson = new Gson();
        return projects.count(gson.toJson(queryObject)) != 0;
    }

    public void updateProjectDocument(ProjectDocument projectDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection projects = jongo.getCollection(BaseProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        projects.withWriteConcern(WriteConcern.SAFE);
        BaseProjectDocument queryObject = new BaseProjectDocument(projectDocument.getOrg(), projectDocument.getName());
        Gson gson = new Gson();
        projects.update(gson.toJson(queryObject)).with(projectDocument);
    }

    public void insertProjectDocument(ProjectDocument projectDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection projects = jongo.getCollection(BaseProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        projects.insert(projectDocument);
    }
}
