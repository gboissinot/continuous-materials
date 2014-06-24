package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.domain.maven;

/**
 * @author Gregory Boissinot
 */
public enum MavenArtifactType {

    BINARY("binary"),
    SOURCES("sources"),
    JAVADOC("javadoc");

    private final String type;

    MavenArtifactType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
