package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import fr.synchrotron.soleil.ica.proxy.utilities.HttpClientProxy;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * Created by Administrateur on 24/06/14.
 */
public class HEADHandler implements Handler<HttpServerRequest> {

    protected final HttpClientProxy httpClientProxy;

    public HEADHandler(HttpClientProxy httpClientProxy) {
        this.httpClientProxy = httpClientProxy;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        httpClientProxy.processHEADRepositoryRequest(request, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                httpClientProxy.sendClientResponse(request, clientResponse);
            }
        });
    }

}
