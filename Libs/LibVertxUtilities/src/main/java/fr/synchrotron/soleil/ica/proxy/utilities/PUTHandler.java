package fr.synchrotron.soleil.ica.proxy.utilities;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.streams.Pump;

/**
 * @author Gregory Boissinot
 */
public class PUTHandler extends HandlerHttpServerRequest {

    public PUTHandler(Vertx vertx, String contextPath, HttpEndpointInfo httpEndpointInfo) {
        super(vertx, contextPath, httpEndpointInfo);
    }

    @Override
    public void handle(final HttpServerRequestWrapper requestWrapper) {
        final HttpServerRequestWrapper.RequestTemplate requestTemplate = requestWrapper.clientTemplate();
        final HttpServerRequest request = requestWrapper.getRequest();

        request.pause();
        final HttpClient httpClient = requestWrapper.getHttpClient();
        final HttpClientRequest vertxHttpClientRequest = httpClient.put(requestTemplate.getClientRequestPath(), requestTemplate.buildPassThroughResponseHandler());
        vertxHttpClientRequest.headers().set(request.headers());
        vertxHttpClientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                requestTemplate.sendError(throwable);
                requestWrapper.closeHttpClient();
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
