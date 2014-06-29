package fr.synchrotron.soleil.ica.ci.service.dornmanagement;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class JenkinsJobHandler implements Handler<HttpServerRequest> {

    private Vertx vertx;

    public JenkinsJobHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(final HttpServerRequest request) {
        //TODO
    }

    private void sendError(HttpServerRequest request, Throwable throwable) {
        request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        if (throwable != null) {
            throwable.printStackTrace();
            request.response().setStatusMessage(throwable.getMessage());
        }
        request.response().end();
    }

    private void sendOK(HttpServerRequest request) {
        request.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code());
        request.response().end();
    }


}
