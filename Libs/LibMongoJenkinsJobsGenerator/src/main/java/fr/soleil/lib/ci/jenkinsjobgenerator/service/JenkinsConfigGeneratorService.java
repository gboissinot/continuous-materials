package fr.soleil.lib.ci.jenkinsjobgenerator.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import fr.soleil.lib.ci.jenkinsjobgenerator.domain.mustache.JenkinsConfig;
import fr.soleil.lib.ci.jenkinsjobgenerator.exception.JenkinsJobGeneratorException;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;

/**
 * Created by ABEILLE on 11/04/2014.
 */
public abstract class JenkinsConfigGeneratorService {

    private Mustache mustache;

    public JenkinsConfigGeneratorService(String templateName) {
        InputStream inputStream = getClass()
                .getResourceAsStream(templateName);
        try {
            mustache = new DefaultMustacheFactory().compile(new StringReader(IOUtils.toString(inputStream)), templateName);
        } catch (IOException ioe) {
            throw new JenkinsJobGeneratorException(ioe);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    protected void compile(Writer writer, JenkinsConfig config) {
        mustache.execute(writer, config);
        try {
            writer.flush();
        } catch (IOException ioe) {
            throw new JenkinsJobGeneratorException(ioe);
        }
    }

    public abstract void load(Writer writer, ProjectDocument projectDocument);
}
