package fr.synchrotron.soleil.ica.ci.service.multirepoproxy;

import fr.synchrotron.soleil.ica.proxy.utilities.GETHandler;
import fr.synchrotron.soleil.ica.proxy.utilities.HttpEndpointInfo;
import fr.synchrotron.soleil.ica.proxy.utilities.HttpServerRequestWrapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

import java.nio.channels.UnresolvedAddressException;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class MutltiGETHandler implements Handler<HttpServerRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(MutltiGETHandler.class);

    private final Vertx vertx;
    private final RepositoryScanner repositoryScanner;
    private final String proxyPath;

    public MutltiGETHandler(Vertx vertx, String proxyPath, List<HttpEndpointInfo> repos) {
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

        final HttpEndpointInfo httpEndpointInfo = repositoryScanner.getRepoFromIndex(repoIndex);

        //We have here a particular situation (reverse proxy).
        //We create an HttpClient object for each server request
        //It is supposed to be lightweight
        final HttpClient httpClient = vertx.createHttpClient()
                .setHost(httpEndpointInfo.getHost())
                .setPort(httpEndpointInfo.getPort())
                .setKeepAlive(false);

        //Sample use case: timeout on client request
        request.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable t) {
                LOG.error("Severe error during request processing :", t);
                request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                request.response().end();
                httpClient.close();
            }
        });

        final HttpServerRequestWrapper requestWrapper
                = new HttpServerRequestWrapper(request, httpClient, proxyPath, httpEndpointInfo, vertx);

        final HttpServerRequestWrapper.RequestTemplate requestTemplate = requestWrapper.clientTemplate();

        HttpClientRequest vertxRequest = httpClient.head(requestTemplate.getClientRequestPath(), new Handler<HttpClientResponse>() {
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
                                    httpClient.close();
                                }
                            });
                        } else {
                            httpClient.close();
                            GETHandler getHandler = new GETHandler(vertx, proxyPath, httpEndpointInfo);
                            getHandler.handle(requestWrapper);
                        }
                        break;
                    case 301:
                    case 404:
                        httpClient.close();
                        processRepository(request, repositoryScanner.getNextIndex(repoIndex));
                        break;
                    default:
                        request.response().setStatusCode(clientResponse.statusCode());
                        request.response().setStatusMessage(clientResponse.statusMessage());
                        request.response().end();
                        httpClient.close();
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
                    requestTemplate.sendError(throwable);
                }
            }

            private boolean isUnreasolvedHost(Throwable throwable) {
                return throwable.getClass().equals(UnresolvedAddressException.class);
            }
        });

        vertxRequest.end();
    }

}
