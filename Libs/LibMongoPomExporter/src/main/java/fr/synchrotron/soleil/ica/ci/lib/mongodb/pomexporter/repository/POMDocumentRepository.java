package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.repository;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.DBObject;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ProjectDocumentDeSerializer;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ProjectDocumentSerializer;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ProjectModule;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.domain.POMDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.exception.POMExporterException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.MongoCollection;
import org.jongo.ResultHandler;
import org.jongo.marshall.jackson.JacksonMapper;

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
//        final Mapper mapper = new JacksonMapper.Builder()
//                .addSerializer(ProjectDocument.class, new ProjectDocumentSerializer())
//                .addDeserializer(ProjectDocument.class, new ProjectDocumentDeSerializer())
//                .build();
        Jongo jongo = new Jongo(mongoDB);

//        Jongo jongo = new Jongo(mongoDB,
//          new JacksonMapper.Builder()
//              .registerModule(new ProjectModule()).build());

        pomDocumentResult.setProjectDocument(loadProjectDocument(jongo, org, name));
        pomDocumentResult.setAritfactDocument(loadArtifactDocument(jongo, org, name, version, status));

        return pomDocumentResult;

    }

    private ArtifactDocument loadArtifactDocument(Jongo jongo, String org, String name, String version, String status) {

        MongoCollection artifacts = jongo.getCollection(ArtifactDocument.MONGO_ARTIFACTS_COLLECTION_NAME);

        ArtifactDocumentKey queryObject = new ArtifactDocumentKey(org, name, version, status);
        Gson gson = ArtifactDocument.getGson();
        final Iterable<ArtifactDocument> artifactDocuments =
                artifacts.find(gson.toJson(queryObject)).as(ArtifactDocument.class);
//                artifacts.find(gson.toJson(queryObject)).map(new ResultHandler<ArtifactDocument>() {
//                    @Override
//                    public ArtifactDocument map(DBObject result) {
//
//                        //result.get("or")
//
//                        return null;
//                    }
//                });
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

        MongoCollection projects = jongo.getCollection(ProjectDocument.MONGO_PROJECTS_COLLECTION_NAME);
        ProjectDocumentKey queryObject = new ProjectDocumentKey(org, name);

        Gson gson = ProjectDocument.getGson();
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
