package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.exception.POMImporterException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.ArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.ProjectRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.apache.maven.model.Model;

import java.io.*;

/**
 * @author Gregory Boissinot
 */
public class POMImportService {

    private ProjectRepository projectRepository;
    private ArtifactRepository artifactRepository;

    public POMImportService(MongoDBDataSource mongoDBDataSource) {
        if (mongoDBDataSource == null) {
            throw new NullPointerException("An mongoDBDataSource element is required.");
        }
        this.projectRepository = new ProjectRepository(mongoDBDataSource);
        this.artifactRepository = new ArtifactRepository(mongoDBDataSource);
    }


    public void importPomFile(String pomContent) {
        StringReader stringReader = new StringReader(pomContent);
        importPomFile(stringReader);
        stringReader.close();
    }

    public void importPomFile(File pomFile) {
        if (pomFile == null) {
            throw new NullPointerException("An pomFile element is required.");
        }
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(pomFile);
            importPomFile(fileReader);
        } catch (FileNotFoundException fne) {
            throw new POMImporterException(fne);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException ioe) {
                    throw new POMImporterException(ioe);
                }
            }
        }
    }

    private void importPomFile(Reader pomReader) {
        PomReaderService pomReaderService = new PomReaderService();
        final Model pomModel = pomReaderService.getModel(pomReader);
        insertProjectDocument(pomModel);
        insertArtifactDocument(pomModel);
    }

    void insertArtifactDocument(Model pomModel) {
        ArtifactDocumentLoaderService artifactDocumentLoaderService = new ArtifactDocumentLoaderService();
        final ArtifactDocument artifactDocument = artifactDocumentLoaderService.populateArtifactDocument(pomModel);

        if (artifactRepository.isArtifactDocumentAlreadyExists(artifactDocument.getKey())) {
            artifactRepository.updateArtifactDocument(artifactDocument);
        } else {
            artifactRepository.insertArtifactDocument(artifactDocument);
        }
    }

    void insertProjectDocument(Model pomModel) {
        ProjectDocumentLoaderService projectDocumentLoaderService = new ProjectDocumentLoaderService();
        final ProjectDocument projectDocument = projectDocumentLoaderService.populateProjectDocument(pomModel);

        if (projectRepository.isProjectDocumentAlreadyExists(projectDocument.getKey())) {
            projectRepository.updateProjectDocument(projectDocument);
        } else {
            projectRepository.insertProjectDocument(projectDocument);
        }
    }

}
