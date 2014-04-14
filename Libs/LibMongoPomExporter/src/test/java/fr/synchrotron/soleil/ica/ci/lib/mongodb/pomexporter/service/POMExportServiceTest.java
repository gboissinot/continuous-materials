package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.service;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.repository.POMDocumentRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.POMImportService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.PomReaderService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.apache.maven.model.Model;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Gregory Boissinot
 */
public class POMExportServiceTest {


    private static DB mongoDB;
    private static Jongo jongo;

    private static POMImportService pomImportService;
    private static POMExportService pomExportService;

    @BeforeClass
    public static void setupMongoDB() throws IOException {
        final Fongo fongo = new Fongo("testMongoServer");
        mongoDB = fongo.getDB("repo");
        jongo = new Jongo(mongoDB);
        final InMemoryMongoDBDataSource mongoDBDataSource = new InMemoryMongoDBDataSource();
        pomImportService = new POMImportService(mongoDBDataSource);
        pomExportService = new POMExportService(new POMDocumentRepository(mongoDBDataSource));
    }

    @After
    public void cleanupProjectsCollection() {
        MongoCollection projectsCollection = jongo.getCollection("projects");
        projectsCollection.remove();

        MongoCollection artifactsCollection = jongo.getCollection("artifacts");
        artifactsCollection.remove();
    }

    static private class InMemoryMongoDBDataSource implements MongoDBDataSource {
        @Override
        public DB getMongoDB() {
            return mongoDB;
        }
    }

    @Test
    public void testExport() throws IOException, URISyntaxException, SAXException {

        //TEST DATA
        StringWriter writer = new StringWriter();
        String org = "fr.synchrotron.soleil.ica.ci.lib";
        String name = "maven-versionresolver";
        String version = "1.0.0";
        String status = "RELEASE";
        File inputPomFile = new File(this.getClass().getResource("pom-1.xml").toURI());
        PomReaderService pomReaderService = new PomReaderService();
        FileReader inputPomFileReader = new FileReader(inputPomFile);
        Model inputPomFileModel = pomReaderService.getModel(inputPomFileReader);
        inputPomFileReader.close();


        pomImportService.importPomFile(inputPomFile);
        pomExportService.exportPomFile(writer, org, name, version, status);

        final String outputPomContent = writer.toString();
        assertNotNull(outputPomContent);
        final StringReader outputPomContentStringReader = new StringReader(outputPomContent);
        Model outputPomFileModel = pomReaderService.getModel(outputPomContentStringReader);
        outputPomContentStringReader.close();


        //assertEquals(inputPomFileModel.getModelVersion(), outputPomFileModel.getModelVersion());
        assertEquals(inputPomFileModel.getGroupId(), outputPomFileModel.getGroupId());
        assertEquals(inputPomFileModel.getArtifactId(), outputPomFileModel.getArtifactId());
        //assertEquals(inputPomFileModel.getVersion(), outputPomFileModel.getVersion());

    }

}
