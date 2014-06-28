package fr.synchrotron.soleil.ica.proxy.utilities;

import org.vertx.java.core.Vertx;

/**
 * @author Gregory Boissinot
 */
public class GETHandler extends HandlerHttpServerRequest {

    public GETHandler(Vertx vertx, String contextPath, HttpEndpointInfo httpEndpointInfo) {
        super(vertx, contextPath, httpEndpointInfo);
    }

    @Override
    public void handle(HttpServerRequestWrapper request) {
        request.clientTemplate().getAndRespond();
    }

}
