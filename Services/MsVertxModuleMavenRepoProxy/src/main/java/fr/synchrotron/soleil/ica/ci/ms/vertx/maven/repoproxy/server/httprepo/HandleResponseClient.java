package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.server.httprepo;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.streams.Pump;

/**
 * @author Gregory Boissinot
 */
public class HandleResponseClient implements Handler<HttpClientResponse> {

    protected HttpServerRequest request;

    public HandleResponseClient(HttpServerRequest request) {
        this.request = request;
    }

    @Override
    public void handle(HttpClientResponse clientResponse) {
        request.response().setStatusCode(clientResponse.statusCode());
        request.response().setStatusMessage(clientResponse.statusMessage());
        request.response().headers().set(clientResponse.headers());
        clientResponse.endHandler(new Handler<Void>() {
            public void handle(Void event) {
                request.response().end();
            }
        });
        Pump.createPump(clientResponse, request.response().setChunked(true)).start();
    }

}
