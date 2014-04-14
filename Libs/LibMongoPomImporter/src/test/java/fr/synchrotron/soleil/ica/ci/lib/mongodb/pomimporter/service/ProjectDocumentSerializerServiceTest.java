package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.DeveloperDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.GsonProjectDocumentSerializer;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Gregory Boissinot
 */
public class ProjectDocumentSerializerServiceTest {

    @Test
    public void testProjectDocument() {
        ProjectDocument projectDocument = new ProjectDocument("org", "name");
        List<DeveloperDocument> developerDocuments = new ArrayList<DeveloperDocument>();

        //Developer1
        DeveloperDocument developerDocument1 = new DeveloperDocument();
        developerDocument1.setId("developer1");
        developerDocument1.setName("Developer 1");
        developerDocument1.setEmail("dev@mail.com");
        developerDocument1.setRoles(Arrays.asList(new String[]{"Project owner", "Project developer"}));

        developerDocuments.add(developerDocument1);
        projectDocument.setDevelopers(developerDocuments);

        //Gson gson = new Gson();
        Gson gson = new GsonBuilder().registerTypeAdapter(ProjectDocument.class, new GsonProjectDocumentSerializer()).create();
        String result = gson.toJson(projectDocument);
        final String expectedMongodDBDocument = "{\"org\":\"org\",\"name\":\"name\",\"developers\":[{\"id\":\"developer1\",\"name\":\"Developer 1\",\"email\":\"dev@mail.com\",\"roles\":[\"Project owner\",\"Project developer\"]}]}";

        assertEquals(expectedMongodDBDocument, result);
    }
}
