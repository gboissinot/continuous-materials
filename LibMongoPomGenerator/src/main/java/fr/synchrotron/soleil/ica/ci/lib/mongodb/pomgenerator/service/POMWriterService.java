package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomgenerator.service;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomgenerator.exception.POMGeneratorException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Gregory Boissinot
 */
public class POMWriterService {

    public void writeModel(Writer writer, Model pomModel) {

        if (pomModel == null) {
            throw new NullPointerException("A pomModel is required.");
        }

        try {
            final MavenXpp3Writer mavenXpp3Writer = new MavenXpp3Writer();
            mavenXpp3Writer.write(writer, pomModel);
        } catch (IOException ioe) {
            throw new POMGeneratorException("Can't write Maven Model", ioe);
        }
    }

}
