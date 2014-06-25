package fr.synchrotron.soleil.ica.proxy.utilities;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.streams.Pump;

/**
 * @author Gregory Boissinot
 */
public class PUTHandler implements Handler<HttpServerRequest> {

    protected final ProxyService proxyService;

    public PUTHandler(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final String path = proxyService.getRequestPath(request);

        request.pause();
        final HttpClientRequest vertxHttpClientRequest = proxyService.getVertxHttpClient().put(path, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                proxyService.sendClientResponse(request, clientResponse);
            }
        });
        vertxHttpClientRequest.headers().set(request.headers());
        vertxHttpClientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                proxyService.sendError(request, throwable);
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
