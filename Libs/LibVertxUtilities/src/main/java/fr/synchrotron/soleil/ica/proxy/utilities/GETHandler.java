package fr.synchrotron.soleil.ica.proxy.utilities;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class GETHandler extends RequestHandlerWrapper {

    public GETHandler(ProxyService proxyService) {
        super(proxyService);
    }

    @Override
    public void handleRequest(final HttpServerRequest request, final HttpClient vertxHttpClient) {
        proxyService.processGETRepositoryRequest(request, vertxHttpClient, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                proxyService.sendClientResponse(request, clientResponse, vertxHttpClient);
            }
        });
    }

}
