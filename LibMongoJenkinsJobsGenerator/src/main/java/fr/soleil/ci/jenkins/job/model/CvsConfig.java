package fr.soleil.ci.jenkins.job.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by ABEILLE on 09/04/2014.
 */
public class CvsConfig {


    private String description;
    private String cvmodulename;
    private String cvsroot;
    private String emails;

    public CvsConfig(String description, String emails, String cvsroot, String cvmodulename) {
        this.description = description;
        this.emails = emails;
        this.cvsroot = cvsroot;
        this.cvmodulename = cvmodulename;
    }

    public String getCvsroot() {
        return cvsroot;
    }

    public String getCvmodulename() {
        return cvmodulename;
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
