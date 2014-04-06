package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.service;

import fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.exception.MavenRepoProxyException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Gregory Boissinot
 */
public class PomModelRetriever {

    public Model getModel(Reader pomReader) {
        try {
            final MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
            return mavenXpp3Reader.read(pomReader);
        } catch (XmlPullParserException xe) {
            new MavenRepoProxyException(xe);
        } catch (IOException ioe) {
            new MavenRepoProxyException(ioe);
        }

        throw new MavenRepoProxyException("Can't read Maven Model");
    }

}
