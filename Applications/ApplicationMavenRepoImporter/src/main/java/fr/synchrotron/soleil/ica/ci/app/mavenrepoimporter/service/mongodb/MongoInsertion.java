package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.mongodb;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Gregory Boissinot
 */
public class MongoInsertion {

    private MongoTemplate mongoTemplate;

    public MongoInsertion(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void insert(ArtifactDocument artifactDocument) {

        mongoTemplate.insert(artifactDocument, "artifacts");
    }
}
