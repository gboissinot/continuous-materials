package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

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
    private String serverProxyPath;

    public HttpArtifactCaller(Vertx vertx,
                              String serverProxyPath,
                              String repoHost, int repoPort, String repoURIPath) {
        this.vertx = vertx;
        this.serverProxyPath = serverProxyPath;
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
        String artifactPath = request.path().substring(serverProxyPath.length() + 1);
        return repoURIPath.endsWith("/") ? (repoURIPath + artifactPath) : (repoURIPath + "/" + artifactPath);
    }

    public HttpClient getVertxHttpClient() {
        return getVertx().createHttpClient()
                .setHost(repoHost)
                .setPort(repoPort)
                .setConnectTimeout(10000);
    }

}
