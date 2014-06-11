package fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities;

import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class RepositoryRequestBuilder {

    private final String repoHost;
    private final int repoPort;
    private final String repoURIPath;
    private String serverProxyPath;

    public RepositoryRequestBuilder(String serverProxyPath, String repoHost, int repoPort, String repoURIPath) {
        this.serverProxyPath = serverProxyPath;
        this.repoHost = repoHost;
        this.repoPort = repoPort;
        this.repoURIPath = repoURIPath;
    }

    public String getRepoHost() {
        return repoHost;
    }

    public String buildRequestPath(final HttpServerRequest request) {
        String artifactPath = request.path().substring(serverProxyPath.length() + 1);
        return repoURIPath.endsWith("/") ? (repoURIPath + artifactPath) : (repoURIPath + "/" + artifactPath);
    }


}
