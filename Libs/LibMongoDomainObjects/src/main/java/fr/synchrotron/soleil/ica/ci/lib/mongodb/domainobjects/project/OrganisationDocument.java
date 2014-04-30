package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project;

/**
 * Created by ABEILLE on 25/04/2014.
 */
public class OrganisationDocument {
    /**
     * The full name of the organization.
     */
    private String name;

    /**
     * The URL to the organization's home page.
     */
    private String url;

    public OrganisationDocument() {
    }

    public OrganisationDocument(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * Get the full name of the organization.
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the full name of the organization.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the URL to the organization's home page.
     *
     * @return String
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Set the URL to the organization's home page.
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
