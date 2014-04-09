package fr.soleil.ci.jenkins;

import fr.soleil.ci.jenkins.job.model.CvsConfigGenerator;
import fr.soleil.ci.jenkins.job.model.SvnConfigGenerator;
import fr.soleil.ci.scm.ScmType;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * TODO manage GIT SCM
 *
 * @author ABEILLE
 */
public class JobGenerator {

    private Logger logger = LoggerFactory.getLogger(JobGenerator.class);

    private String jenkinsURL;
    private String user;
    private String password;

    // TODO parameters
    private static String TEMPLATE_SVN = "templateSVN";
    private static String TEMPLATE_CVS = "templateCVS";
    private  CvsConfigGenerator configGenerator ;
    private SvnConfigGenerator configGeneratorSVN;

    public JobGenerator(String jenkinsURL, String user, String password) throws IOException {
        this.jenkinsURL = jenkinsURL;
        this.user = user;
        this.password = password;
        configGenerator = new CvsConfigGenerator();
        configGeneratorSVN = new SvnConfigGenerator();
    }

    public void processJob(ProjectDocument projectDocument) {
        String jenkinsJobName = JobUtilities.getJobName(projectDocument);
        logger.debug("processing job {}", jenkinsJobName);
        int rep = 0;
        try {
            boolean createJob = !isJenkinsJobExists(jenkinsJobName);
            logger.debug("is job exists {}", createJob);
            rep = createJob(ScmType.getScmType(projectDocument), projectDocument, createJob);
        } catch (IOException e) {
            logger.error("error for job {}", jenkinsJobName);
            logger.error("", e);
        } catch (JAXBException e) {
            logger.error("error for job {}", jenkinsJobName);
            logger.error("", e);
        }
        logger.debug("{} result is {}", jenkinsJobName, rep);
    }


    private int createJob(ScmType scmType, ProjectDocument projectDocument, boolean createJob)
            throws IOException, JAXBException {
        int responseCode = 00;
        URL url;
        String jobName = JobUtilities.getJobName(projectDocument);
        if (createJob) {
            url = new URL(jenkinsURL + "/createItem?name=" + jobName);
        } else {
            url = new URL(jenkinsURL + "/job/" + jobName + "/config.xml");
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {

            JobUtilities.addAuth(conn, user, password);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            Writer writer = new OutputStreamWriter(os);
            switch (scmType) {
                case CVS:
                    configGenerator.loadFromMongoToJenkins(writer, projectDocument);
                    break;
                case SVN:
                    configGeneratorSVN.loadFromMongoToJenkins(writer, projectDocument);
                    break;
                default:
                    //TODO
                    break;
            }
            os.flush();
            responseCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            logger.debug("server output is:");
            while ((output = br.readLine()) != null) {
                logger.debug(output);
            }
        } finally {
            conn.disconnect();
        }
        return responseCode;
    }

    private boolean isJenkinsJobExists(String jobName) throws IOException,
            JAXBException {
        URL jobURL = new URL(jenkinsURL + "/job/" + jobName + "/config.xml");
        HttpURLConnection conn = (HttpURLConnection) jobURL.openConnection();
        boolean isJenkinsJobExists = false;
        try {
            JobUtilities.addAuth(conn, user, password);
            conn.setRequestMethod("GET");
            conn.getInputStream();
            isJenkinsJobExists = true;
        } catch (FileNotFoundException e) {
            // job does not exist
            isJenkinsJobExists = false;
        } finally {
            conn.disconnect();
        }
        return isJenkinsJobExists;
    }
}
