package fr.soleil.ci.jenkins;

import fr.soleil.ci.mongodb.project.ProjectLoader;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runner {

    private static String MONGODB_HOSTNAME = System.getProperty("fr.soleil.ci.mongodb.hostname");
    private static String MONGODB_PORT= System.getProperty("fr.soleil.ci.mongodb.port");
    private static String JENKINS_URL= System.getProperty("fr.soleil.ci.jenkins.url");
    private static String JENKINS_USER= System.getProperty("fr.soleil.ci.jenkins.user");
    private static String JENKINS_PWD= System.getProperty("fr.soleil.ci.jenkins.pwd");
	private Logger logger = LoggerFactory.getLogger(Runner.class);

	/**
	 * Load all projects from mongodb and create their jenkins jobs
	 */
	public void createAllJobs() {
        try {
        JobGenerator jobGenerator = new JobGenerator(JENKINS_URL,
                JENKINS_USER, JENKINS_PWD);

		// load projects from mongodb
		ProjectLoader loader = new ProjectLoader(MONGODB_HOSTNAME, Integer.valueOf(MONGODB_PORT));
		Iterable<ProjectDocument> projects = loader.loadProjects();
        // process jenkins jobs
		for (ProjectDocument projectDocument : projects) {
            try {
                jobGenerator.processJob(projectDocument);
            }catch (Throwable e){
                logger.error("could not process job {}",JobUtilities.getJobName(projectDocument));
                logger.error("error is: ",e);
            }
		}
        }catch (Throwable e){
            logger.error("error is: ",e);
        }
	}

	public static void main(String[] args) {
     //   "http://172.16.5.6:8080"
        //"admin", "admin"
        //"172.16.5.7", 27001
		new Runner().createAllJobs();

	}

}
