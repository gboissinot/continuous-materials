package fr.soleil.ci.jenkins.job.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by ABEILLE on 09/04/2014.
 */
public class SvnConfig {

    private String description;



    private String svnurl;
    private String emails;

    public SvnConfig(String description, String emails, String svnurl) {
        this.description = description;
        this.emails = emails;
        this.svnurl = svnurl;
    }

    public String getSvnurl() {
        return svnurl;
    }

    public String getEmails() {
        return emails;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
