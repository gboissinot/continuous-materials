package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.repository;

import com.mongodb.DB;
import com.mongodb.WriteConcern;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

/**
 * @author Gregory Boissinot
 */
public class POMRepository {

    private MongoDBDataSource mongoDBDataSource;

    public POMRepository(MongoDBDataSource mongoDBDataSource) {
        this.mongoDBDataSource = mongoDBDataSource;
    }

    /*
     *   Artifact Document
     */

    public boolean isArtifactDocumentAlreadyExists(ArtifactDocument artifactDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection artifacts = jongo.getCollection("artifacts");
        String criteria = "{\"org\":\"" + artifactDocument.getOrganisation()
                + "\", \"name\":\"" + artifactDocument.getName()
                + "\", \"status\":\"" + artifactDocument.getStatus()
                + "\", \"version\":\"" + artifactDocument.getVersion()
                + "\"}";
        return artifacts.count(criteria) != 0;
    }

    public void updateOrInsertArtifactDocument(ArtifactDocument artifactDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection artifacts = jongo.getCollection("artifacts");
        artifacts.withWriteConcern(WriteConcern.SAFE);
        String criteria = "{\"org\":\"" + artifactDocument.getOrganisation()
                + "\", \"name\":\"" + artifactDocument.getName()
                + "\", \"status\":\"" + artifactDocument.getStatus()
                + "\", \"version\":\"" + artifactDocument.getVersion()
                + "\"}";
        artifacts.update(criteria).with(artifactDocument);
    }

    public void insertArtifactDocument(ArtifactDocument artifactDocument) {
         DB mongoDB = mongoDBDataSource.getMongoDB();
         Jongo jongo = new Jongo(mongoDB);
         MongoCollection artifacts = jongo.getCollection("artifacts");
         artifacts.insert(artifactDocument);
     }

    /*
     *   Project Document
     */

    public boolean isProjectDocumentAlreadyExists(ProjectDocument projectDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection projects = jongo.getCollection("projects");
        String criteria = "{\"org\":\"" + projectDocument.getOrg() + "\", \"name\":\"" + projectDocument.getName() + "\"}";
        return projects.count(criteria) != 0;
    }

    public void updateOrInsertProjectDocument(ProjectDocument projectDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection projects = jongo.getCollection("projects");
        projects.withWriteConcern(WriteConcern.SAFE);
        String criteria = "{\"org\":\"" + projectDocument.getOrg() + "\", \"name\":\"" + projectDocument.getName() + "\"}";
        projects.update(criteria).with(projectDocument);
    }

    public void insertProjectDocument(ProjectDocument projectDocument) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection projects = jongo.getCollection("projects");
        projects.insert(projectDocument);
    }
}
