package fr.soleil.lib.ci.jenkinsjobgenerator.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by ABEILLE on 11/04/2014.
 */
public class JenkinsConfig {
    private String description;
    private String emails;

    public JenkinsConfig(String description, String emails) {
        this.description = description;
        this.emails = emails;
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
