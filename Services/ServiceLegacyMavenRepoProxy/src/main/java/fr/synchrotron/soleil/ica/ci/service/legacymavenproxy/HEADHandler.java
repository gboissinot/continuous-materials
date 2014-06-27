package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import fr.synchrotron.soleil.ica.proxy.utilities.ProxyService;
import fr.synchrotron.soleil.ica.proxy.utilities.RequestHandlerWrapper;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * Created by Administrateur on 24/06/14.
 */
public class HEADHandler extends RequestHandlerWrapper {

    public HEADHandler(ProxyService proxyService) {
        super(proxyService);
    }

    @Override
    public void handleRequest(final HttpServerRequest request, final HttpClient vertxHttpClient) {

        proxyService.processHEADRepositoryRequest(request, vertxHttpClient, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                proxyService.sendClientResponse(request, clientResponse, vertxHttpClient);
            }
        });
    }

}
