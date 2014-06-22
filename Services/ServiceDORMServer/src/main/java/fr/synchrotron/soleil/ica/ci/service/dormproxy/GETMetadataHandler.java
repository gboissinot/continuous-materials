package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class GETMetadataHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {

        String path = request.path();
        System.out.println("GET " + path);

        request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
        request.response().end();

    }
}
