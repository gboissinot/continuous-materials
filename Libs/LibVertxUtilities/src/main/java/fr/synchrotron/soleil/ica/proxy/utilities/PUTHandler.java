package fr.synchrotron.soleil.ica.proxy.utilities;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.streams.Pump;

/**
 * @author Gregory Boissinot
 */
public class PUTHandler extends RequestHandlerWrapper {

    public PUTHandler(ProxyService proxyService) {
        super(proxyService);
    }

    @Override
    public void handleRequest(final HttpServerRequest request, final HttpClient vertxHttpClient) {
        final String path = proxyService.getRequestPath(request);

        request.pause();
        final HttpClientRequest vertxHttpClientRequest = vertxHttpClient.put(path, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                proxyService.sendClientResponse(request, clientResponse, vertxHttpClient);
            }
        });
        vertxHttpClientRequest.headers().set(request.headers());
        vertxHttpClientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                proxyService.sendError(request, throwable);
                vertxHttpClient.close();
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
