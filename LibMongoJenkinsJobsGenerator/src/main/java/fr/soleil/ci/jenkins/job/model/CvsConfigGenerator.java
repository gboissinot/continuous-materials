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
public class CvsConfigGenerator {

    private Logger logger = LoggerFactory.getLogger(CvsConfigGenerator.class);
    private Mustache mustache;

    public CvsConfigGenerator() throws IOException {
        InputStream inputStream = getClass()
                .getResourceAsStream("mustache-template-config-java-cvs.xml");
        try {
            mustache = new DefaultMustacheFactory().compile(new StringReader(IOUtils.toString(inputStream)), "cvsconfig");
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public void loadFromMongoToJenkins(Writer writer, ProjectDocument projectDocument)
            throws IOException, JAXBException {
        logger.debug("loading project {}", projectDocument);
        String jenkinsJobName = JobUtilities.getJobName(projectDocument);
        // "scm:cvs:pserver:anonymous:@ganymede.synchrotron-soleil.fr:/usr/local/CVS:DeviceServer/Generic/Tests/ErrorGenerator"
        String cvsURL = projectDocument.getScmConnection().replace(
                ScmType.SCM_CVS, "");

        String cvsRoot = cvsURL.substring(0, cvsURL.lastIndexOf(":"));
        String moduleName = cvsURL.substring(cvsURL.lastIndexOf(":") + 1,
                cvsURL.length());
        logger.debug("job CVS root is: {} - module name is: {}", cvsRoot,
                moduleName);

        CvsConfig config = new CvsConfig(projectDocument.getDescription(), JobUtilities.getEmails(projectDocument), cvsRoot, moduleName);

        logger.debug("jenkins config is {}", config);
        // create jenkins job
        mustache.execute(writer, config);
        writer.flush();
    }
}
