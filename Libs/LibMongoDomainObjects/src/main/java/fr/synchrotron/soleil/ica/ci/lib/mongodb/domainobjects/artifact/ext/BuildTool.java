package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.maven.MavenProjectInfo;

/**
 * @author Gregory Boissinot
 */
public class BuildTool {

    private MavenProjectInfo maven;

    public MavenProjectInfo getMaven() {
        return maven;
    }

    public void setMaven(MavenProjectInfo maven) {
        this.maven = maven;
    }
}
