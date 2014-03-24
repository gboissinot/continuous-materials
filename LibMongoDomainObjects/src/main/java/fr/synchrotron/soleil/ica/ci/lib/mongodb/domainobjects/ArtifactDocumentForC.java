package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects;

/**
 * @author Gregory Boissinot
 */
public class ArtifactDocumentForC {

    private String archi;

    private String platform;

    private String compiler;

    private String typeDep;

    private String mod;

    public ArtifactDocumentForC(String archi, String platform, String compiler, String typeDep, String mod) {
        this.archi = archi;
        this.platform = platform;
        this.compiler = compiler;
        this.typeDep = typeDep;
        this.mod = mod;
    }

    public String getArchi() {
        return archi;
    }

    public void setArchi(String archi) {
        this.archi = archi;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCompiler() {
        return compiler;
    }

    public void setCompiler(String compiler) {
        this.compiler = compiler;
    }

    public String getTypeDep() {
        return typeDep;
    }

    public void setTypeDep(String typeDep) {
        this.typeDep = typeDep;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }
}
