package fr.soleil.lib.ci.jenkinsjobgenerator.domain.mustache;

/**
 * Created by ABEILLE on 09/04/2014.
 */
public class JenkinsCvsConfig extends JenkinsConfig{


    private String cvmodulename;
    private String cvsroot;

    public JenkinsCvsConfig(String description, String emails, String cvsroot, String cvmodulename) {
       super(description,emails);
        this.cvsroot = cvsroot;
        this.cvmodulename = cvmodulename;
    }

    public String getCvsroot() {
        return cvsroot;
    }

    public String getCvmodulename() {
        return cvmodulename;
    }



}
