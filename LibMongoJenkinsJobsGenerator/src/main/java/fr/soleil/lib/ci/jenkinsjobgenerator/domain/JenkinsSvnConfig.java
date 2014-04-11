package fr.soleil.lib.ci.jenkinsjobgenerator.domain;

/**
 * Created by ABEILLE on 09/04/2014.
 */
public class JenkinsSvnConfig extends JenkinsConfig{

    private String svnurl;

    public JenkinsSvnConfig(String description, String emails, String svnurl) {
      super(description,emails);
        this.svnurl = svnurl;
    }

    public String getSvnurl() {
        return svnurl;
    }

}
