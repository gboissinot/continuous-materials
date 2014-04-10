package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import com.github.fakemongo.Fongo;
import com.google.gson.Gson;
import com.mongodb.DB;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDependency;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.DeveloperDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.repository.POMImportRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Gregory Boissinot
 */
public class POMImportServiceTest {

    private static DB mongoDB;
    private static Jongo jongo;
    private static POMImportService pomImportService;

    @BeforeClass
    public static void setupMongoDB() throws IOException {
        Fongo fongo = new Fongo("testMongoServer");
        mongoDB = fongo.getDB("repo");
        jongo = new Jongo(mongoDB);
        pomImportService = new POMImportService(new POMImportRepository(new InMemoryMongoDBDataSource()));
    }

    @After
    public void cleanupProjectsCollection() {
        MongoCollection projectsCollection = jongo.getCollection("projects");
        projectsCollection.remove();
    }

    static private class InMemoryMongoDBDataSource implements MongoDBDataSource {
        @Override
        public DB getMongoDB() {
            return mongoDB;
        }
    }

    private Iterable<ArtifactDocument> getArtifactDocument() {
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection artifactsCollection = jongo.getCollection("artifacts");
        final ArtifactDocument artifactDocument = new ArtifactDocument();
        artifactDocument.setOrg("fr.synchrotron.soleil.ica.ci.lib");
        artifactDocument.setName("maven-versionresolver");
        artifactDocument.setVersion("1.0.1");
        artifactDocument.setStatus("INTEGRATION");
        Gson gson = new Gson();
        return artifactsCollection.find(gson.toJson(artifactDocument)).as(ArtifactDocument.class);
    }


    private Iterable<ProjectDocument> getProjectDocument() {
        Jongo jongo = new Jongo(mongoDB);
        MongoCollection projectsCollection = jongo.getCollection("projects");
        final ProjectDocument projectDocument = new ProjectDocument();
        projectDocument.setOrg("fr.synchrotron.soleil.ica.ci.lib");
        projectDocument.setName("maven-versionresolver");
        Gson gson = new Gson();
        return projectsCollection.find(gson.toJson(projectDocument)).as(ProjectDocument.class);
    }

    @Test
    public void artifactDocumentInsertion() throws Exception {

        URL resource = this.getClass().getResource("pom-1.xml");
        File pomFile = new File(resource.toURI());
        FileReader pomFileReader = new FileReader(pomFile);
        PomReaderService pomReaderService = new PomReaderService();
        pomImportService.insertArtifactDocument(pomReaderService.getModel(pomFileReader));
        pomFileReader.close();

        final Iterable<ArtifactDocument> artifactDocumentIterable = getArtifactDocument();
        final Iterator<ArtifactDocument> iterator = artifactDocumentIterable.iterator();
        assertTrue(iterator.hasNext());
        final ArtifactDocument artifactDocument = iterator.next();
        assertFalse(iterator.hasNext());

        assertEquals("fr.synchrotron.soleil.ica.ci.lib", artifactDocument.getOrg());
        assertEquals("maven-versionresolver", artifactDocument.getName());

        assertEquals("1.0.1", artifactDocument.getVersion());
        assertEquals("INTEGRATION", artifactDocument.getStatus());

        final List<ArtifactDependency> dependencies = artifactDocument.getDependencies();
        assertNotNull(dependencies);
        assertNotEquals(0, dependencies.size());

        boolean depMongoDbDriverPresent = false;
        for (ArtifactDependency dependency : dependencies) {
            final String org = dependency.getOrg();
            final String name = dependency.getName();
            if ("org.mongodb".equals(org) && "mongo-java-driver".equals(name)) {
                assertNull(dependency.getVersion());
                depMongoDbDriverPresent = true;
            }
        }
        assertTrue(depMongoDbDriverPresent);
    }


    @Test
    public void projectDocumentInsertion() throws Exception {

        URL resource = this.getClass().getResource("pom-1.xml");
        File pomFile = new File(resource.toURI());
        FileReader pomFileReader = new FileReader(pomFile);
        PomReaderService pomReaderService = new PomReaderService();
        pomImportService.insertProjectDocument(pomReaderService.getModel(pomFileReader));
        pomFileReader.close();

        final Iterable<ProjectDocument> projectDocumentIterable = getProjectDocument();
        final Iterator<ProjectDocument> iterator = projectDocumentIterable.iterator();
        assertTrue(iterator.hasNext());
        final ProjectDocument projectDocument = iterator.next();
        assertFalse(iterator.hasNext());
        assertEquals("fr.synchrotron.soleil.ica.ci.lib", projectDocument.getOrg());
        assertEquals("maven-versionresolver", projectDocument.getName());
        assertEquals("Maven Version Resolver", projectDocument.getDescription());
        assertEquals("https://github.com/synchrotron-soleil-ica/maven-versionresolver.git", projectDocument.getScmConnection());

        final List<DeveloperDocument> developers = projectDocument.getDevelopers();
        assertEquals(1, developers.size());
        DeveloperDocument developerDocument = developers.get(0);
        assertEquals("gbois", developerDocument.getId());
        assertEquals("Gregory Boissinot", developerDocument.getName());
        assertEquals("gregory.boissinot@gmail.com", developerDocument.getEmail());
        assertEquals(2, developerDocument.getRoles().size());

    }


    @Test
    public void projectDocumentInsertionOrUpdate() throws Exception {

        URL resource1 = this.getClass().getResource("pom-1.xml");
        File pomFile1 = new File(resource1.toURI());
        URL resource2 = this.getClass().getResource("pom-2.xml");
        File pomFile2 = new File(resource2.toURI());


        PomReaderService pomReaderService = new PomReaderService();
        FileReader pomFileReader1 = new FileReader(pomFile1);
        pomImportService.insertProjectDocument(pomReaderService.getModel(pomFileReader1));
        pomFileReader1.close();

        //Insert the same project with some modification
        FileReader pomFileReader2 = new FileReader(pomFile2);
        pomImportService.insertProjectDocument(pomReaderService.getModel(pomFileReader2));
        pomFileReader2.close();

        final Iterable<ProjectDocument> projectDocumentIterable = getProjectDocument();
        final Iterator<ProjectDocument> iterator = projectDocumentIterable.iterator();
        assertTrue(iterator.hasNext());
        final ProjectDocument projectDocument = iterator.next();
        //Always only one document
        assertFalse(iterator.hasNext());
        assertEquals("fr.synchrotron.soleil.ica.ci.lib", projectDocument.getOrg());
        assertEquals("maven-versionresolver", projectDocument.getName());
        assertEquals("Maven Version Resolver", projectDocument.getDescription());
        assertEquals("https://github.com/synchrotron-soleil-ica/maven-versionresolver.git", projectDocument.getScmConnection());

        final List<DeveloperDocument> developers = projectDocument.getDevelopers();
        assertEquals(1, developers.size());
        DeveloperDocument developerDocument = developers.get(0);
        assertEquals("gbois2", developerDocument.getId());
        assertEquals("Gregory Boissinot", developerDocument.getName());
        assertEquals("gregory.boissinot@gmail.com", developerDocument.getEmail());
        assertEquals(2, developerDocument.getRoles().size());

    }
}
