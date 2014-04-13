package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.exception;

/**
 * @author Gregory Boissinot
 */
public class MavenRepoProxyException extends RuntimeException {

    public MavenRepoProxyException() {
    }

    public MavenRepoProxyException(String s) {
        super(s);
    }

    public MavenRepoProxyException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public MavenRepoProxyException(Throwable throwable) {
        super(throwable);
    }
}
