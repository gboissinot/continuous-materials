package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.repository;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.domain.POMDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.ArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.ProjectRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;

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
        ArtifactRepository artifactRepository = new ArtifactRepository(mongoDBDataSource);
        ProjectRepository projectRepository = new ProjectRepository(mongoDBDataSource);
        pomDocumentResult.setProjectDocument(projectRepository.findProjectDocument(org, name));
        pomDocumentResult.setAritfactDocument(artifactRepository.findArtifactDocument(new ArtifactDocumentKey(org, name, version, status)));

        return pomDocumentResult;
    }


}
