package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext;

import java.util.Date;

/**
 * @author Gregory Boissinot
 */
public class BuildContext {

    private BuildTool buildTool;

    private Date buildtime;

    public BuildTool getBuildTool() {
        return buildTool;
    }

    public void setBuildTool(BuildTool buildTool) {
        this.buildTool = buildTool;
    }

    public Date getBuildtime() {
        return buildtime;
    }

    public void setBuildtime(Date buildtime) {
        this.buildtime = buildtime;
    }
}
