package fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.*;
import org.vertx.java.core.streams.Pump;

/**
 * @author Gregory Boissinot
 */
public class PUTHandler implements Handler<HttpServerRequest> {

    protected final Vertx vertx;
    protected final HttpClient vertxHttpClient;
    protected final RepositoryRequestBuilder repositoryRequestBuilder;

    public PUTHandler(Vertx vertx,
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
        System.out.println("Upload " + path);

        request.pause();
        final HttpClientRequest vertxHttpClientRequest = vertxHttpClient.put(path, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                request.response().setStatusCode(clientResponse.statusCode());
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                final String setCookie = clientResponse.headers().get(HttpHeaders.SET_COOKIE);
                if (setCookie != null) {
                    request.response().headers().set(HttpHeaders.SET_COOKIE, repositoryRequestBuilder.getNewCookieContent(setCookie));
                }
                clientResponse.dataHandler(new Handler<Buffer>() {
                    public void handle(Buffer data) {
                        request.response().write(data);
                    }
                });
                clientResponse.endHandler(new Handler<Void>() {
                    public void handle(Void event) {
                        request.response().end();
                    }
                });
            }
        });

        vertxHttpClientRequest.headers().set(request.headers());
        //TODO REMOVE IT
        //vertxHttpClientRequest.setChunked(true);
        vertxHttpClientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                request.response().end();
            }
        });

        request.endHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                vertxHttpClientRequest.end();
            }
        });

        final Pump pump = Pump.createPump(request, vertxHttpClientRequest);
        pump.start();
        request.resume();
    }

}
