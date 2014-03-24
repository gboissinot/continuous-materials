package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.exception.POMImporterException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.repository.POMRepository;
import org.apache.maven.model.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public class POMManagementService {

    private POMRepository pomRepository;

    public POMManagementService(POMRepository pomRepository) {
        this.pomRepository = pomRepository;
        this.pomReaderService = new PomReaderService();
    }

    private PomReaderService pomReaderService;


    public void insertArtifactDocument(File pomFile) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(pomFile);
            final Model pomModel = pomReaderService.getModel(fileReader);
            ArtifactDocumentLoaderService artifactDocumentLoaderService = new ArtifactDocumentLoaderService();
            final ArtifactDocument artifactDocument = artifactDocumentLoaderService.loadPomModel(pomModel);

            if (pomRepository.isArtifactDocumentAlreadyExists(artifactDocument)) {
                pomRepository.updateOrInsertArtifactDocument(artifactDocument);
            } else {
                pomRepository.insertArtifactDocument(artifactDocument);
            }


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

    public void insertProjectDocument(File pomFile) {

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(pomFile);

            final Model pomModel = pomReaderService.getModel(fileReader);
            ProjectDocumentLoaderService projectDocumentLoaderService = new ProjectDocumentLoaderService();
            final ProjectDocument projectDocument = projectDocumentLoaderService.loadPomModel(pomModel);

            if (pomRepository.isProjectDocumentAlreadyExists(projectDocument)) {
                pomRepository.updateOrInsertProjectDocument(projectDocument);
            } else {
                pomRepository.insertProjectDocument(projectDocument);
            }

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

}
