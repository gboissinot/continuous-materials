package fr.soleil.lib.ci.jenkinsjobgenerator.domain.mustache;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;

public enum ScmType {

    CVS, SVN, GIT, UNKNOWN;

    public static final String SCM_SVN = "scm:svn:";

    public static final String SCM_CVS = "scm:cvs:";
    public static final String SCM_GIT = "git";

    public static ScmType getScmType(ProjectDocument projectDocument) {
        ScmType result = ScmType.UNKNOWN;
        if (projectDocument.getScmConnection().startsWith(SCM_CVS)) {
            result = ScmType.CVS;
        } else if (projectDocument.getScmConnection().startsWith(SCM_SVN)) {
            result = ScmType.SVN;
        } else if (projectDocument.getScmConnection().startsWith(SCM_GIT)) {
            result = ScmType.GIT;
        }
        return result;
    }
}
