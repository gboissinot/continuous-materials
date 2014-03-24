package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects;

/**
 * @author Gregory Boissinot
 */
public class ArtifactDocumentForJava {

    private boolean isSourcesExists;

    private boolean isJavaDocExists;

    public boolean isSourcesExists() {
        return isSourcesExists;
    }

    public void setSourcesExists(boolean sourcesExists) {
        isSourcesExists = sourcesExists;
    }

    public boolean isJavaDocExists() {
        return isJavaDocExists;
    }

    public void setJavaDocExists(boolean javaDocExists) {
        isJavaDocExists = javaDocExists;
    }
}
