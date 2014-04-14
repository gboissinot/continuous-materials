package fr.soleil.lib.ci.jenkinsjobgenerator.service;


import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.DeveloperDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ABEILLE on 11/04/2014.
 */
public class JenkinsCvsConfigGeneratorServiceTest {
    private static ProjectDocument projectDocument = new ProjectDocument();

    @BeforeClass
    public static void loadDummyProject() {
        projectDocument = new ProjectDocument();
        projectDocument.setDescription("toto");
        projectDocument.setName("test1");
        projectDocument.setOrg("fr.soleil.test");
        projectDocument
                .setScmConnection("scm:cvs:pserver:anonymous:@ganymede.synchrotron-soleil.fr:/usr/local/CVS:DeviceServer/Generic/Tests/ErrorGenerator");
        List<DeveloperDocument> developers = new ArrayList<DeveloperDocument>();
        DeveloperDocument dev1 = new DeveloperDocument();
        dev1.setId("id");
        dev1.setEmail("toto@soleil.fr");
        dev1.setName("toto");
        developers.add(dev1);
        DeveloperDocument dev2 = new DeveloperDocument();
        dev2.setId("id2");
        dev2.setEmail("titi@soleil.fr");
        dev2.setName("titi");
        developers.add(dev2);
        projectDocument.setDevelopers(developers);
        projectDocument.setLanguage("java");
    }

    @Test
    public void testCVSDescription() {
        JenkinsCvsConfigGeneratorService cvsService = new JenkinsCvsConfigGeneratorService();
        projectDocument.setDescription("desc");
        Writer writer = new StringWriter();
        cvsService.load(writer, projectDocument);
        String xmlString = writer.toString();
        Assert.assertThat(xmlString, StringContains.containsString("<description>desc</description>"));
    }

    @Test
    public void testCVSSCMConnection() {
        JenkinsCvsConfigGeneratorService cvsService = new JenkinsCvsConfigGeneratorService();

        projectDocument
                .setScmConnection("scm:cvs:pserver:anonymous:@ganymede.synchrotron-soleil.fr:/usr/local/CVS:DeviceServer/Generic/Tests/Test");
        Writer writer = new StringWriter();
        cvsService.load(writer, projectDocument);
        String xmlString = writer.toString();
        Assert.assertThat(xmlString, StringContains.containsString("<remoteName>DeviceServer/Generic/Tests/Test</remoteName>"));
        Assert.assertThat(xmlString, StringContains.containsString("<cvsRoot>pserver:anonymous:@ganymede.synchrotron-soleil.fr:/usr/local/CVS</cvsRoot>"));

    }

    @Test
    public void testCVSDevelopers() {
        List<DeveloperDocument> developers = new ArrayList<DeveloperDocument>();
        DeveloperDocument dev1 = new DeveloperDocument();
        dev1.setId("id");
        dev1.setEmail("toto@soleil.fr");
        dev1.setName("toto");
        developers.add(dev1);
        DeveloperDocument dev2 = new DeveloperDocument();
        dev2.setId("id2");
        dev2.setEmail("titi@soleil.fr");
        dev2.setName("titi");
        developers.add(dev2);
        DeveloperDocument dev3 = new DeveloperDocument();
        dev3.setId("id3");
        developers.add(dev3);
        projectDocument.setDevelopers(developers);
        JenkinsCvsConfigGeneratorService cvsService = new JenkinsCvsConfigGeneratorService();
        Writer writer = new StringWriter();
        cvsService.load(writer, projectDocument);
        String xmlString = writer.toString();
        Assert.assertThat(xmlString, StringContains.containsString("<recipients>toto@soleil.fr titi@soleil.fr id3@synchrotron-soleil.fr </recipients>"));
    }

}
