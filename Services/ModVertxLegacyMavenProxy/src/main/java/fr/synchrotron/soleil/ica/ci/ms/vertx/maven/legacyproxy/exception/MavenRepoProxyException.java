package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.legacyproxy.exception;

/**
 * @author Gregory Boissinot
 */
public class MavenRepoProxyException extends RuntimeException {

    public MavenRepoProxyException(String s) {
        super(s);
    }

    public MavenRepoProxyException(Throwable throwable) {
        super(throwable);
    }
}
