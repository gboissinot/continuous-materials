package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import fr.synchrotron.soleil.ica.proxy.utilities.HandlerHttpServerRequest;
import fr.synchrotron.soleil.ica.proxy.utilities.HttpEndpointInfo;
import fr.synchrotron.soleil.ica.proxy.utilities.HttpServerRequestWrapper;
import org.vertx.java.core.Vertx;


/**
 * @author Gregory Boissinot
 */
public class HEADHandler extends HandlerHttpServerRequest {

    public HEADHandler(Vertx vertx, String contextPath, HttpEndpointInfo httpEndpointInfo) {
        super(vertx, contextPath, httpEndpointInfo);
    }

    @Override
    public void handle(HttpServerRequestWrapper request) {
        request.clientTemplate().headAndRespond();
    }

}
