package fr.synchrotron.soleil.ica.ci.service.multirepoproxy;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.streams.Pump;

import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ProxyRequestHandler implements Handler<HttpServerRequest> {

    private final Vertx vertx;

    private final RepositoryScanner repositoryScanner;

    public ProxyRequestHandler(Vertx vertx, List<RepositoryObject> repos) {
        this.vertx = vertx;

        if (repos == null || repos.size() == 0) {
            throw new IllegalArgumentException("repos");
        }
        this.repositoryScanner = new RepositoryScanner(repos);
    }

    @Override
    public void handle(final HttpServerRequest request) {
        processRepo(request, 0);
    }

    private void processRepo(final HttpServerRequest request,
                             final int repoIndex) {

        final RepositoryObject repositoryInfo = repositoryScanner.getRepoFromIndex(repoIndex);

        final HttpClient vertxHttpClient = vertx.createHttpClient();
        vertxHttpClient.setHost(repositoryInfo.getHost()).setPort(repositoryInfo.getPort());

        final String repoURIPath = repositoryInfo.getUri();
        HttpClientRequest vertxRequest = vertxHttpClient.get(buildRequestPath(request, repoURIPath), new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {

                if (HttpResponseStatus.NOT_FOUND.code() == clientResponse.statusCode()
                        || HttpResponseStatus.MOVED_PERMANENTLY.code() == clientResponse.statusCode()) {
                    if (repositoryScanner.isLastRepo(repoIndex)) {
                        request.response().setStatusCode(clientResponse.statusCode());
                        request.response().setStatusMessage("Artifact NOT FOUND");
                        request.response().end();
                    } else {
                        processRepo(request, repositoryScanner.getNextIndex(repoIndex));
                    }

                } else if (HttpResponseStatus.OK.code() == clientResponse.statusCode()) {
                    makeGetRepoRequest(request, vertxHttpClient, repoURIPath);
                } else {
                    request.response().setStatusCode(clientResponse.statusCode());
                    request.response().setStatusMessage(clientResponse.statusMessage());
                    request.response().end();
                }
            }


        });

        vertxRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable e) {
                request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("Exception from ").append(repositoryInfo);
                errorMsg.append("-->").append(e.toString());
                errorMsg.append("\n");
                request.response().end(errorMsg.toString());
            }
        });

        vertxRequest.end();
    }

    private String buildRequestPath(final HttpServerRequest request, String repoURIPath) {
        final String prefix = RepoProxyHttpEndpointVerticle.PROXY_PATH;
        String artifactPath = request.path().substring(prefix.length() + 1);
        return repoURIPath.endsWith("/") ? (repoURIPath + artifactPath) : (repoURIPath + "/" + artifactPath);
    }

    private void makeGetRepoRequest(final HttpServerRequest request, final HttpClient vertxHttpClient, final String repoUri) {

        HttpClientRequest vertxRequest = vertxHttpClient.get(buildRequestPath(request, repoUri), new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                final int statusCode = clientResponse.statusCode();
                request.response().setStatusCode(statusCode);
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                clientResponse.endHandler(new Handler<Void>() {
                    public void handle(Void event) {
                        request.response().end();
                    }
                });
                if (statusCode == HttpResponseStatus.NOT_MODIFIED.code()
                        || statusCode == HttpResponseStatus.OK.code()) {
                    //Send result to original client
                    Pump.createPump(clientResponse, request.response().setChunked(true)).start();
                }
            }
        });

        vertxRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable e) {
                request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("Exception from ").append(repoUri);
                errorMsg.append("-->").append(e.toString());
                errorMsg.append("\n");
                request.response().end(errorMsg.toString());
            }
        });

        vertxRequest.end();
    }

}
