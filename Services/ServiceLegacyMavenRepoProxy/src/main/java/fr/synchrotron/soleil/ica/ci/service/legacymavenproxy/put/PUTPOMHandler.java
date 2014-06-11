package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.put;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.ServiceAddressRegistry;
import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.PUTHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class PUTPOMHandler extends PUTHandler {

    public PUTPOMHandler(Vertx vertx, String proxyPath, String repoHost, int repoPort, String repoUri) {
        super(vertx, proxyPath, repoHost, repoPort, repoUri);
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final String path = repositoryRequestBuilder.buildRequestPath(request);
        System.out.println("Upload " + path);

        final HttpClientRequest vertxHttpClientRequest = vertxHttpClient.put(path, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                final int statusCode = clientResponse.statusCode();
                request.response().setStatusCode(statusCode);
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                final String setCookie = clientResponse.headers().get(HttpHeaders.SET_COOKIE);
                if (setCookie != null) {
                    request.response().headers().set(HttpHeaders.SET_COOKIE, repositoryRequestBuilder.getNewCookieContent(setCookie));
                }
                clientResponse.endHandler(new Handler<Void>() {
                    public void handle(Void event) {
                        request.response().end();
                    }
                });
            }
        });

        vertxHttpClientRequest.headers().set(request.headers());
        vertxHttpClientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                request.response().end();
            }
        });

        final Buffer body = new Buffer();
        request.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer data) {
                body.appendBuffer(data);
                vertxHttpClientRequest.write(data);
            }
        });

        request.endHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                vertxHttpClientRequest.end();
                vertx.eventBus().send(ServiceAddressRegistry.EB_ADDRESS_TRACK_POM_SERVICE, body.toString());
            }
        });

    }

}
