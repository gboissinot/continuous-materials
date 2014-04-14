package fr.soleil.lib.ci.jenkinsjobgenerator.service;


import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.DeveloperDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;

import java.net.HttpURLConnection;
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
        return projectDocument.getKey().getOrg() + "." + projectDocument.getKey().getName() + "_" + projectDocument.getLanguage();
    }

    public static void addAuth(HttpURLConnection conn, String user,
                               String password) {
        String userPassword = user + ":" + password;
        String encoding = new sun.misc.BASE64Encoder().encode(userPassword
                .getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encoding);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/xml");
    }
}
