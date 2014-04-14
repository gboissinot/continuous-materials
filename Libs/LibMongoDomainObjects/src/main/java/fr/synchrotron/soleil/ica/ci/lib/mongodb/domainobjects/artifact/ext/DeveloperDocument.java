package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext;

import java.util.List;


/**
 * @author Gregory Boissinot
 */
public class DeveloperDocument {

    /**
     * The unique ID of the developer in the SCM.
     */
    private String id;

    /**
     * The full name of the contributor.
     */
    private String name;

    /**
     * The email address of the contributor.
     */
    private String email;

    /**
     * The URL for the homepage of the contributor.
     */
    private String url;

    /**
     * The organization to which the contributor belongs.
     */
    private String organization;

    /**
     * The URL of the organization.
     */
    private String organizationUrl;

    /**
     * Field roles.
     */
    private java.util.List/*<String>*/ roles;

    /**
     * The timezone the contributor is in. This is a number in the
     * range -11 to 12.
     */
    private String timezone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganizationUrl() {
        return organizationUrl;
    }

    public void setOrganizationUrl(String organizationUrl) {
        this.organizationUrl = organizationUrl;
    }

    public List getRoles() {
        return roles;
    }

    public void setRoles(List roles) {
        this.roles = roles;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
