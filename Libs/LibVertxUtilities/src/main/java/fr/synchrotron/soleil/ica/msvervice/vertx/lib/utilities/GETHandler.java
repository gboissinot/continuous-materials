package fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.*;
import org.vertx.java.core.streams.Pump;

/**
 * @author Gregory Boissinot
 */
public class GETHandler implements Handler<HttpServerRequest> {

    protected final Vertx vertx;
    protected final HttpClient vertxHttpClient;
    protected final RepositoryRequestBuilder repositoryRequestBuilder;

    public GETHandler(Vertx vertx,
                      String proxyPath,
                      String repoHost,
                      int repoPort,
                      String repoUri) {
        this.vertx = vertx;
        this.vertxHttpClient = vertx.createHttpClient().setHost(repoHost).setPort(repoPort);
        this.repositoryRequestBuilder = new RepositoryRequestBuilder(proxyPath, repoHost, repoPort, repoUri);
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final String path = repositoryRequestBuilder.buildRequestPath(request);
        System.out.println("Download " + path);

        HttpClientRequest vertxHttpClientRequest = vertxHttpClient.get(path, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {

                clientResponse.pause();
                final int statusCode = clientResponse.statusCode();
                request.response().setStatusCode(statusCode);
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                request.response().setChunked(true);
                final String setCookie = clientResponse.headers().get(HttpHeaders.SET_COOKIE);
                if (setCookie != null) {
                    request.response().headers().set(HttpHeaders.SET_COOKIE, repositoryRequestBuilder.getNewCookieContent(setCookie));
                }
                clientResponse.endHandler(new Handler<Void>() {
                    public void handle(Void event) {
                        request.response().end();
                    }
                });
                if (statusCode == HttpResponseStatus.NOT_MODIFIED.code()
                        || statusCode == HttpResponseStatus.OK.code()) {
                    final Pump pump = Pump.createPump(clientResponse, request.response());
                    pump.start();
                }
                clientResponse.resume();
            }
        });

        vertxHttpClientRequest.headers().set(request.headers());
        vertxHttpClientRequest.headers().remove("Host");
        vertxHttpClientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                request.response().end();
            }
        });
        vertxHttpClientRequest.end();

    }
}
