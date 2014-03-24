package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.exception.POMImporterException;
import org.apache.maven.model.Model;
import org.junit.After;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static junit.framework.Assert.assertNotNull;

/**
 * @author Gregory Boissinot
 */
public class PomReaderServiceTest {

    private PomReaderService pomReaderService = new PomReaderService();

    private InputStreamReader pomAsInputStreamReader;

    @After
    public void cleanup() {
        if (pomAsInputStreamReader != null) {
            try {
                pomAsInputStreamReader.close();
            } catch (IOException e) {
                //Ignore
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void nullReader() {
        pomReaderService.getModel(null);
    }

    @Test
    public void readPom() throws FileNotFoundException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("pom-1.xml");
        pomAsInputStreamReader = new InputStreamReader(resourceAsStream);
        Model model = pomReaderService.getModel(pomAsInputStreamReader);
        assertNotNull(model);
    }

    @Test(expected = POMImporterException.class)
    public void wrongPom() throws FileNotFoundException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("wrongpom.xml");
        pomAsInputStreamReader = new InputStreamReader(resourceAsStream);
        Model model = pomReaderService.getModel(pomAsInputStreamReader);
        assertNotNull(model);
    }


}
