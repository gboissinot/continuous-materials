package fr.synchrotron.soleil.ica.proxy.utilities;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class GETHandler implements Handler<HttpServerRequest> {

    protected final HttpClientProxy httpClientProxy;

    public GETHandler(HttpClientProxy httpClientProxy) {
        this.httpClientProxy = httpClientProxy;
    }

    @Override
    public void handle(final HttpServerRequest request) {
        httpClientProxy.processGETRepositoryRequest(request, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                httpClientProxy.sendClientResponse(request, clientResponse);
            }
        });
    }

}
