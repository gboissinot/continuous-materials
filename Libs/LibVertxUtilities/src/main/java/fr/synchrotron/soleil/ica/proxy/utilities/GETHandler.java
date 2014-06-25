package fr.synchrotron.soleil.ica.proxy.utilities;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class GETHandler implements Handler<HttpServerRequest> {

    protected final ProxyService proxyService;

    public GETHandler(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Override
    public void handle(final HttpServerRequest request) {
        proxyService.processGETRepositoryRequest(request, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                proxyService.sendClientResponse(request, clientResponse);
            }
        });
    }

}
