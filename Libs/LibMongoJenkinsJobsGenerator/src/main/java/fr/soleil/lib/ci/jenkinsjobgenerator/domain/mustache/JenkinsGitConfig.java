package fr.soleil.lib.ci.jenkinsjobgenerator.domain.mustache;

/**
 * Created by ABEILLE on 09/04/2014.
 */
public class JenkinsGitConfig extends JenkinsConfig {

    private String giturl;

    public JenkinsGitConfig(String description, String emails, String giturl) {
        super(description, emails);
        this.giturl = giturl;
    }

    public String getGiturl() {
        return giturl;
    }
}
