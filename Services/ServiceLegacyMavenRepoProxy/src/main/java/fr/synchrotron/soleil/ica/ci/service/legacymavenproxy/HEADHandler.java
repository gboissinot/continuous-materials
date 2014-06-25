package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import fr.synchrotron.soleil.ica.proxy.utilities.ProxyService;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * Created by Administrateur on 24/06/14.
 */
public class HEADHandler implements Handler<HttpServerRequest> {

    protected final ProxyService proxyService;

    public HEADHandler(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        proxyService.processHEADRepositoryRequest(request, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                proxyService.sendClientResponse(request, clientResponse);
            }
        });
    }

}
