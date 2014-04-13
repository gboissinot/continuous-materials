package fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.exception;

/**
 * @author Gregory Boissinot
 */
public class MavenVersionResolverException extends RuntimeException {

    public MavenVersionResolverException(String message) {
        super(message);
    }

    public MavenVersionResolverException(Throwable throwable) {
        super(throwable);
    }

}
