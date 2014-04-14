package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext;

/**
 * @author Gregory Boissinot
 */
public class BuildContext {

    private BuildTool buildTool;

    public BuildTool getBuildTool() {
        return buildTool;
    }

    public void setBuildTool(BuildTool buildTool) {
        this.buildTool = buildTool;
    }
}
