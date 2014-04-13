package fr.soleil.lib.ci.jenkinsjobgenerator.service;

import fr.soleil.lib.ci.jenkinsjobgenerator.domain.JenkinsSvnConfig;
import fr.soleil.lib.ci.jenkinsjobgenerator.scm.ScmType;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by ABEILLE on 09/04/2014.
 */
public class JenkinsSvnConfigGeneratorService extends JenkinsConfigGeneratorService {

    private Logger logger = LoggerFactory.getLogger(JenkinsSvnConfigGeneratorService.class);

    public JenkinsSvnConfigGeneratorService() throws IOException {
        super("mustache-template-config-java-svn.xml");
    }

    @Override
    public void load(Writer writer, ProjectDocument projectDocument) {
        logger.debug("loading project {}", projectDocument);
        String svnURL = projectDocument.getScmConnection().replace(
                ScmType.SCM_SVN, "");
        JenkinsSvnConfig config = new JenkinsSvnConfig(projectDocument.getDescription(), JobUtilities.getEmails(projectDocument), svnURL);
        logger.debug("jenkins config is {}", config);
        // create jenkins job
        compile(writer, config);
    }
}
