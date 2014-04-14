package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.repository;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.WriteConcern;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

/**
 * @author Gregory Boissinot
 */
public class POMImportRepository {

    private MongoDBDataSource mongoDBDataSource;

    private Jongo jongo;

    public POMImportRepository(MongoDBDataSource mongoDBDataSource) {
        this.mongoDBDataSource = mongoDBDataSource;
        DB mongoDB = mongoDBDataSource.getMongoDB();
        jongo = new Jongo(mongoDB);
//        jongo = new Jongo(mongoDB,
//                new JacksonMapper.Builder()
//                        .registerModule(new ProjectModule()).build()
//        );

    }

    /*
     *   Artifact Document
     */

    public boolean isArtifactDocumentAlreadyExists(ArtifactDocument artifactDocument) {
        MongoCollection artifacts = jongo.getCollection(ArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        Gson gson = ArtifactDocument.getGson();
        return artifacts.count(gson.toJson(artifactDocument.getKey())) != 0;
    }

    public void updateArtifactDocument(ArtifactDocument artifactDocument) {
        MongoCollection artifacts = jongo.getCollection(ArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        artifacts.withWriteConcern(WriteConcern.SAFE);
        Gson gson = ArtifactDocument.getGson();
        artifacts.update(gson.toJson(artifactDocument.getKey())).with(artifactDocument);
    }

    public void insertArtifactDocument(ArtifactDocument artifactDocument) {
        MongoCollection artifacts = jongo.getCollection(ArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);
        Gson gson = ArtifactDocument.getGson();
        artifacts.insert(gson.toJson(artifactDocument));
    }

    /*
     *   Project Document
     */

    public boolean isProjectDocumentAlreadyExists(ProjectDocument projectDocument) {
        MongoCollection projects = jongo.getCollection(ProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        Gson gson = ProjectDocument.getGson();
        return projects.count(gson.toJson(projectDocument.getKey())) != 0;
    }

    public void updateProjectDocument(ProjectDocument projectDocument) {
        MongoCollection projects = jongo.getCollection(ProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        projects.withWriteConcern(WriteConcern.SAFE);
        Gson gson = ProjectDocument.getGson();
        projects.update(gson.toJson(projectDocument.getKey())).with(projectDocument);
    }

    public void insertProjectDocument(ProjectDocument projectDocument) {
        MongoCollection projects = jongo.getCollection(ProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        projects.insert(projectDocument);
    }
}
