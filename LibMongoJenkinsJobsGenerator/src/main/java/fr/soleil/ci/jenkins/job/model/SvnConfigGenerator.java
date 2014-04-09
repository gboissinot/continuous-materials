package fr.soleil.ci.jenkins.job.model;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import fr.soleil.ci.jenkins.JobUtilities;
import fr.soleil.ci.scm.ScmType;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;

/**
 * Created by ABEILLE on 09/04/2014.
 */
public class SvnConfigGenerator {

    private Logger logger = LoggerFactory.getLogger(SvnConfigGenerator.class);
    private Mustache mustache;

    public SvnConfigGenerator() throws IOException {
        InputStream inputStream = getClass()
                .getResourceAsStream("mustache-template-config-java-svn.xml");
        try {
            mustache = new DefaultMustacheFactory().compile(new StringReader(IOUtils.toString(inputStream)), "scvnconfig");
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public void loadFromMongoToJenkins(Writer writer, ProjectDocument projectDocument)
            throws IOException, JAXBException {
        logger.debug("loading project {}", projectDocument);
        String jenkinsJobName = JobUtilities.getJobName(projectDocument);
        String svnURL = projectDocument.getScmConnection().replace(
                ScmType.SCM_SVN, "");
        logger.debug("job svnURL: {}", svnURL);
        SvnConfig config = new SvnConfig(projectDocument.getDescription(), JobUtilities.getEmails(projectDocument), svnURL);
        logger.debug("jenkins config is {}", config);
        // create jenkins job
        mustache.execute(writer, config);
        writer.flush();
    }
}
