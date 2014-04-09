package fr.soleil.ci.scm;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;

public enum ScmType {
	CVS, SVN, GIT, UNKNOWN;

	public static final String SCM_SVN = "scm:svn:";

	public static final String SCM_CVS = "scm:cvs:";

	public static ScmType getScmType(ProjectDocument projectDocument) {
		ScmType result = ScmType.UNKNOWN;
		if (projectDocument.getScmConnection().startsWith(SCM_CVS)) {
			result = ScmType.CVS;
		} else if (projectDocument.getScmConnection().startsWith(SCM_SVN)) {
			result = ScmType.SVN;
		}
		return result;
	}
}
