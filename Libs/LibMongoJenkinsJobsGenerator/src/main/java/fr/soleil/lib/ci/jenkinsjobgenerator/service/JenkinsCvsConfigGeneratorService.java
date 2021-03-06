package fr.soleil.lib.ci.jenkinsjobgenerator.service;

import fr.soleil.lib.ci.jenkinsjobgenerator.domain.mustache.JenkinsCvsConfig;
import fr.soleil.lib.ci.jenkinsjobgenerator.domain.mustache.ScmType;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;

/**
 * Created by ABEILLE on 09/04/2014.
 */
public class JenkinsCvsConfigGeneratorService extends JenkinsConfigGeneratorService {

    private Logger logger = LoggerFactory.getLogger(JenkinsCvsConfigGeneratorService.class);

    public JenkinsCvsConfigGeneratorService() {
        super("mustache-template-config-java-cvs.xml");
    }

    @Override
    public void load(Writer writer, ProjectDocument projectDocument) {
        logger.debug("loading project {}", projectDocument);

        String cvsURL = projectDocument.getScmConnection().replace(ScmType.SCM_CVS, "");
        String cvsRoot = cvsURL.substring(0, cvsURL.lastIndexOf(":"));
        String moduleName = cvsURL.substring(cvsURL.lastIndexOf(":") + 1, cvsURL.length());
        logger.debug("job CVS root is: {} - module name is: {}", cvsRoot, moduleName);

        JenkinsCvsConfig config = new JenkinsCvsConfig(projectDocument.getDescription(), JobUtilities.getEmails(projectDocument), cvsRoot, moduleName);

        logger.debug("jenkins config is {}", config);

        compile(writer, config);
    }
}
