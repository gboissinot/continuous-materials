package fr.synchrotron.soleil.ica.ci.service.multirepoproxy;

import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.GETHandler;
import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.RepositoryObject;
import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.RepositoryRequestBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;

import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ProxyRequestPullHandler implements Handler<HttpServerRequest> {

    private final Vertx vertx;
    private final RepositoryScanner repositoryScanner;
    private final String proxyPath;

    public ProxyRequestPullHandler(Vertx vertx, String proxyPath, List<RepositoryObject> repos) {
        this.vertx = vertx;

        if (repos == null || repos.size() == 0) {
            throw new IllegalArgumentException("repos");
        }
        this.repositoryScanner = new RepositoryScanner(repos);
        this.proxyPath = proxyPath;
    }

    @Override
    public void handle(final HttpServerRequest request) {
        processRepository(request, 0);
    }

    private void processRepository(final HttpServerRequest request,
                                   final int repoIndex) {

        if (repositoryScanner.isLastRepo(repoIndex)) {
            request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
            request.response().setStatusMessage("Artifact NOT FOUND");
            request.response().end();
            return;
        }

        final RepositoryObject repositoryInfo = repositoryScanner.getRepoFromIndex(repoIndex);
        final HttpClient vertxHttpClient = vertx.createHttpClient();
        vertxHttpClient.setHost(repositoryInfo.getHost()).setPort(repositoryInfo.getPort());

        RepositoryRequestBuilder repositoryRequestBuilder =
                new RepositoryRequestBuilder(proxyPath, repositoryInfo);

        HttpClientRequest vertxRequest = vertxHttpClient.head(repositoryRequestBuilder.buildRequestPath(request), new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {

                switch (clientResponse.statusCode()) {
                    case 200:
                        if ("HEAD".equals(request.method())) {
                            request.response().setStatusCode(clientResponse.statusCode());
                            request.response().headers().set(clientResponse.headers());
                            clientResponse.endHandler(new Handler<Void>() {
                                public void handle(Void event) {
                                    request.response().end();
                                }
                            });
                        } else {
                            makeGetRepoRequest(request, repositoryInfo);
                        }
                        break;
                    case 301:
                    case 404:
                        processRepository(request, repositoryScanner.getNextIndex(repoIndex));
                        break;
                    default:
                        request.response().setStatusCode(clientResponse.statusCode());
                        request.response().setStatusMessage(clientResponse.statusMessage());
                        request.response().end();
                        break;
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

    private void makeGetRepoRequest(final HttpServerRequest request, RepositoryObject repositoryInfo) {
        GETHandler getHandler = new GETHandler(vertx, proxyPath, repositoryInfo.getHost(), repositoryInfo.getPort(), repositoryInfo.getUri());
        getHandler.handle(request);
    }

}
