package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.legacyproxy.server.httprepo;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class HttpArtifactCaller {

    private final Vertx vertx;
    private final String repoHost;
    private final int repoPort;
    private final String repoURIPath;

    public HttpArtifactCaller(Vertx vertx,
                              String repoHost, int repoPort, String repoURIPath) {
        this.vertx = vertx;
        this.repoHost = repoHost;
        this.repoPort = repoPort;
        this.repoURIPath = repoURIPath;
    }


    public Vertx getVertx() {
        return vertx;
    }

    public String getRepoHost() {
        return repoHost;
    }

    public String buildRequestPath(final HttpServerRequest request) {

        final String prefix = "/maven";
        String artifactPath = request.path().substring(prefix.length() + 1);
        return repoURIPath.endsWith("/") ? (repoURIPath + artifactPath) : (repoURIPath + "/" + artifactPath);
    }

    public HttpClient getPClient() {
        return getVertx().createHttpClient()
                .setHost(repoHost)
                .setPort(repoPort)
                .setConnectTimeout(10000);
    }

}
