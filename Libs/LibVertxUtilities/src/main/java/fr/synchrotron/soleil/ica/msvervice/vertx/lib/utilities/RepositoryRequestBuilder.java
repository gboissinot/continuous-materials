package fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities;

import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class RepositoryRequestBuilder {

    private final String serverProxyPath;
    private final RepositoryObject repositoryObject;

    public RepositoryRequestBuilder(String serverProxyPath, RepositoryObject repositoryObject) {
        this.serverProxyPath = serverProxyPath;
        this.repositoryObject = repositoryObject;
    }

    public RepositoryRequestBuilder(String serverProxyPath, String host, int port, String uri) {
        this.serverProxyPath = serverProxyPath;
        this.repositoryObject = new RepositoryObject(host, port, uri);
    }

    public String getServerProxyPath() {
        return serverProxyPath;
    }

    public RepositoryObject getRepositoryObject() {
        return repositoryObject;
    }

    public String buildRequestPath(final HttpServerRequest request) {
        String artifactPath = request.path().substring(serverProxyPath.length() + 1);
        String repoURIPath = repositoryObject.getUri();
        return repoURIPath.endsWith("/") ? (repoURIPath + artifactPath) : (repoURIPath + "/" + artifactPath);
    }

}
