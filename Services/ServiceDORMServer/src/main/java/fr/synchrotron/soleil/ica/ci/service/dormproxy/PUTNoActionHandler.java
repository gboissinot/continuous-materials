package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;


/**
 * @author Gregory Boissinot
 */
public class PUTNoActionHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {
        request.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code());
        request.response().end();
    }
}
