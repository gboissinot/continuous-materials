package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.service;

import fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.exception.MavenRepoProxyException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.vertx.java.core.http.HttpServerRequest;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @author Gregory Boissinot
 */
public class PomArtifactWriter {

    public void writePomArtifact(HttpServerRequest request, Model model) {
        StringWriter stringWriter = new StringWriter();
        try {
            new MavenXpp3Writer().write(stringWriter, model);
            final String pomContent = stringWriter.toString();
            request.response().putHeader("Content-Length", String.valueOf(pomContent.getBytes().length));
            request.response().end(pomContent);
        } catch (IOException ioe) {
            throw new MavenRepoProxyException(ioe);
        } finally {
            try {
                stringWriter.close();
            } catch (IOException ioe) {
                throw new MavenRepoProxyException(ioe);
            }
        }
    }

}
