package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects;


import java.util.Date;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ArtifactDocument {

    @Required
    @Order(1)
    private String organisation;

    @Required
    @Order(2)
    private String name;

    @Order(3)
    private String version;

    @Required
    @Order(4)
    private String status;

    @Order(5)
    private String type;

    @Order(6)
    private boolean isThirdParty;

    @Required
    @Order(7)
    private Date creationDate;

    @Required
    @Order(8)
    private Date publicationDate;

    @Order(9)
    private String sha1;

    @Order(10)
    private String md5;

    @Order(11)
    private String description;

    @Order(12)
    private String fileExtension;

    @Order(13)
    private long fileSize;

    @Order(14)
    private boolean isForce;

    @Order(15)
    private ArtifactDocumentForJava javaLanguage;

    @Order(16)
    private ArtifactDocumentForC cLanguage;

    private List<ArtifactDependency> dependencies;

    @Override
    public String toString() {
        return "ArtifactDocument{" +
                "organisation='" + organisation + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isThirdParty() {
        return isThirdParty;
    }

    public void setThirdParty(boolean isThirdParty) {
        this.isThirdParty = isThirdParty;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean isForce) {
        this.isForce = isForce;
    }

    public ArtifactDocumentForJava getJavaLanguage() {
        return javaLanguage;
    }

    public void setJavaLanguage(ArtifactDocumentForJava javaLanguage) {
        this.javaLanguage = javaLanguage;
    }

    public ArtifactDocumentForC getcLanguage() {
        return cLanguage;
    }

    public void setcLanguage(ArtifactDocumentForC cLanguage) {
        this.cLanguage = cLanguage;
    }

    public List<ArtifactDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<ArtifactDependency> dependencies) {
        this.dependencies = dependencies;
    }
}
