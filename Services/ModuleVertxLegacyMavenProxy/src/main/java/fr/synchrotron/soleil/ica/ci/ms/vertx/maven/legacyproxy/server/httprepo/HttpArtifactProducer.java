package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.legacyproxy.server.httprepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;

/**
 * @author Gregory Boissinot
 */
@Component
@Scope("singleton")
@Profile("repoHttp")
public class HttpArtifactProducer {

    @Autowired
    @Value("${repo.http.host}")
    protected String repoHost;

    @Autowired
    @Value("${repo.http.port}")
    protected int repoPort;

    @Autowired
    @Value("${repo.uri.path}")
    protected String repoURIPath;

    protected Logger logger;

    private Vertx vertx;

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    protected String buildRequestPath(final HttpServerRequest request) {

        final String prefix = "/maven";
        String artifactPath = request.path().substring(prefix.length() + 1);
        return repoURIPath.endsWith("/") ? (repoURIPath + "/") : repoURIPath + artifactPath;
    }

    protected HttpClient getPClient() {
        return vertx.createHttpClient()
                .setHost(repoHost)
                .setPort(repoPort)
                .setConnectTimeout(10000);
    }

}
