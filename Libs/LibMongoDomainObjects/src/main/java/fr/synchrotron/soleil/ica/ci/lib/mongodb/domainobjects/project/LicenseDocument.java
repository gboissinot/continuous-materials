package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project;

/**
 * Created by ABEILLE on 25/04/2014.
 */
public class LicenseDocument {

    /**
     * The full legal name of the license.
     */
    private String name;

    /**
     * The official url for the license text.
     */
    private String url;

    /**
     * The primary method by which this project may be
     * distributed.
     * <dl>
     * <dt>repo</dt>
     * <dd>may be downloaded from the Maven
     * repository</dd>
     * <dt>manual</dt>
     * <dd>user must manually download and install
     * the dependency.</dd>
     * </dl>
     */
    private String distribution;

    /**
     * Addendum information pertaining to this license.
     */
    private String comments;

    public LicenseDocument() {
    }

    public LicenseDocument(String name, String url, String distribution, String comments) {
        this.name = name;
        this.url = url;
        this.distribution = distribution;
        this.comments = comments;
    }

    /**
     * Get addendum information pertaining to this license.
     *
     * @return String
     */
    public String getComments() {
        return this.comments;
    }

    /**
     * Set addendum information pertaining to this license.
     *
     * @param comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Get the primary method by which this project may be
     * distributed.
     * <dl>
     * <dt>repo</dt>
     * <dd>may be downloaded from the Maven
     * repository</dd>
     * <dt>manual</dt>
     * <dd>user must manually download and install
     * the dependency.</dd>
     * </dl>
     *
     * @return String
     */
    public String getDistribution() {
        return this.distribution;
    }

    /**
     * Set the primary method by which this project may be
     * distributed.
     * <dl>
     * <dt>repo</dt>
     * <dd>may be downloaded from the Maven
     * repository</dd>
     * <dt>manual</dt>
     * <dd>user must manually download and install
     * the dependency.</dd>
     * </dl>
     *
     * @param distribution
     */
    public void setDistribution(String distribution) {
        this.distribution = distribution;
    } //-- void setDistribution( String )

    /**
     * Get the full legal name of the license.
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the full legal name of the license.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the official url for the license text.
     *
     * @return String
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Set the official url for the license text.
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
