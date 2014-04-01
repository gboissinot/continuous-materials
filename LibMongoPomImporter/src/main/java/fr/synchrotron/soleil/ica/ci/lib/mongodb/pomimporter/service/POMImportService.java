package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.exception.POMImporterException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.repository.POMImportRepository;
import org.apache.maven.model.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public class POMImportService {

    private POMImportRepository pomImportRepository;
    private PomReaderService pomReaderService;

    public POMImportService(POMImportRepository pomImportRepository) {
        this.pomImportRepository = pomImportRepository;
        this.pomReaderService = new PomReaderService();
    }

    public void importPomFile(File pomFile) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(pomFile);
            final Model pomModel = pomReaderService.getModel(fileReader);
            insertProjectDocument(pomModel);
            insertArtifactDocument(pomModel);
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

    void insertArtifactDocument(Model pomModel) {
        ArtifactDocumentLoaderService artifactDocumentLoaderService = new ArtifactDocumentLoaderService();
        final ArtifactDocument artifactDocument = artifactDocumentLoaderService.loadPomModel(pomModel);

        if (pomImportRepository.isArtifactDocumentAlreadyExists(artifactDocument)) {
            pomImportRepository.updateArtifactDocument(artifactDocument);
        } else {
            pomImportRepository.insertArtifactDocument(artifactDocument);
        }
    }

    void insertProjectDocument(Model pomModel) {
        ProjectDocumentLoaderService projectDocumentLoaderService = new ProjectDocumentLoaderService();
        final ProjectDocument projectDocument = projectDocumentLoaderService.loadPomModel(pomModel);

        if (pomImportRepository.isProjectDocumentAlreadyExists(projectDocument)) {
            pomImportRepository.updateProjectDocument(projectDocument);
        } else {
            pomImportRepository.insertProjectDocument(projectDocument);
        }
    }

}
