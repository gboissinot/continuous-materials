package fr.synchrotron.soleil.ica.ci.service.multirepoproxy;

import fr.synchrotron.soleil.ica.proxy.utilities.GETHandler;
import fr.synchrotron.soleil.ica.proxy.utilities.ProxyService;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;

import java.nio.channels.UnresolvedAddressException;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class MutltiGETHandler implements Handler<HttpServerRequest> {

    private final Vertx vertx;
    private final RepositoryScanner repositoryScanner;
    private final String proxyPath;

    public MutltiGETHandler(Vertx vertx, String proxyPath, List<RepositoryObject> repos) {
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

        System.out.println("Trying to download " + request.path() + "from " + repositoryScanner.getRepoFromIndex(repoIndex));

        final RepositoryObject repositoryInfo = repositoryScanner.getRepoFromIndex(repoIndex);
        final ProxyService proxyService = new ProxyService(vertx, proxyPath, repositoryInfo.getHost(), repositoryInfo.getPort(), repositoryInfo.getUri());
        final HttpClient vertxHttpClient = proxyService.getVertxHttpClient();
        HttpClientRequest vertxRequest = vertxHttpClient.head(proxyService.getRequestPath(request), new Handler<HttpClientResponse>() {
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
            public void handle(Throwable throwable) {
                if (isUnreasolvedHost(throwable)) {
                    processRepository(request, repositoryScanner.getNextIndex(repoIndex));
                } else {
                    proxyService.sendError(request, throwable);
                }
            }

            private boolean isUnreasolvedHost(Throwable throwable) {
                return throwable.getClass().equals(UnresolvedAddressException.class);
            }
        });

        vertxRequest.end();
    }

    private void makeGetRepoRequest(final HttpServerRequest request, RepositoryObject repositoryInfo) {
        System.out.println("Downloding " + request.path() + " from " + repositoryInfo);
        GETHandler getHandler = new GETHandler(new ProxyService(vertx, proxyPath, repositoryInfo.getHost(), repositoryInfo.getPort(), repositoryInfo.getUri()));
        getHandler.handle(request);
    }

}
