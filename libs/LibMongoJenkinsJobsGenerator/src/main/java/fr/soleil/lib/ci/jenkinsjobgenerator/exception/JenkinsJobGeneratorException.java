package fr.soleil.lib.ci.jenkinsjobgenerator.exception;

/**
 * Created by ABEILLE on 11/04/2014.
 */
public class JenkinsJobGeneratorException extends  RuntimeException {


    public JenkinsJobGeneratorException() {
    }

    public JenkinsJobGeneratorException(String message) {
        super(message);
    }

    public JenkinsJobGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public JenkinsJobGeneratorException(Throwable cause) {
        super(cause);
    }

    public JenkinsJobGeneratorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
