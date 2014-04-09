package fr.soleil.ci.jenkins;


import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.DeveloperDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.List;

public class JobUtilities {


    //private static final Logger LOGGER = LoggerFactory.getLogger(JobUtilities.class);
    private static final String SYNCHROTRON_SOLEIL_FR = "@synchrotron-soleil.fr";

    private JobUtilities() {

    }

    public static String getEmails(ProjectDocument projectDocument) {
        List<DeveloperDocument> developers = projectDocument.getDevelopers();
        String emails = "";
        if (developers != null) {
            StringBuilder sb = new StringBuilder();
            for (DeveloperDocument developerDocument : developers) {
                if (developerDocument.getEmail().isEmpty()) {
                    sb.append(developerDocument.getId()).append(
                            SYNCHROTRON_SOLEIL_FR);
                } else {
                    sb.append(developerDocument.getEmail());
                }
                sb.append(" ");
            }
            emails = sb.toString();
        }
        return emails;
    }

    public static String getJobName(ProjectDocument projectDocument) {
        return projectDocument.getOrg() + "." + projectDocument.getName() + "_" + projectDocument.getMaven().getPackaging();
    }



//    public static int createJob(String jobName,
//                                Object config, boolean isCreate, String jenkinsURL, String user, String s)
//            throws IOException, JAXBException {
//        int responseCode = 00;
//        URL url;
//        if (isCreate) {
//            url = new URL(jenkinsURL + "/createItem?name=" + jobName);
//        } else {
//            url = new URL(jenkinsURL + "/job/" + jobName + "/config.xml");
//        }
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        try {
//
//            JobUtilities.addAuth(conn, user, s);
//            conn.setRequestMethod("POST");
//            OutputStream os = conn.getOutputStream();
//            if (config instanceof fr.soleil.ci.jenkins.job.model.svn.Project) {
//                fr.soleil.ci.jenkins.job.model.svn.JobProjectLoader
//                        .jaxbObjectToXML((fr.soleil.ci.jenkins.job.model.svn.Project) config, os);
//            } else if (config instanceof Project) {
//                fr.soleil.ci.jenkins.job.model.cvs.JobProjectLoader
//                        .jaxbObjectToXML((Project) config, os);
//            }
//            os.flush();
//            responseCode = conn.getResponseCode();
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    (conn.getInputStream())));
//
//            String output;
//            LOGGER.debug("server output is:");
//            while ((output = br.readLine()) != null) {
//                LOGGER.debug(output);
//            }
//        } finally {
//            conn.disconnect();
//        }
//        return responseCode;
//    }

    public static void addAuth(HttpURLConnection conn, String user,
                               String password) throws ProtocolException {
        String userpassword = user + ":" + password;
        String encoding = new sun.misc.BASE64Encoder().encode(userpassword
                .getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encoding);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/xml");
    }
}
