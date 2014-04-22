package fr.synchrotron.soleil.ica.msvervice.management.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public abstract class AbstractHandler implements Handler<HttpServerRequest> {

    protected void buildStringReplyMessage(final AsyncResult<Message<String>> replyMessage, final HttpServerRequest request) {
        if (replyMessage.succeeded()) {
            request.response().setStatusCode(HttpResponseStatus.OK.code());
            request.response().setStatusMessage(HttpResponseStatus.OK.reasonPhrase());
            String okMessage = String.valueOf(replyMessage.result().body()) + "\n";
            request.response().putHeader("Content-Length", String.valueOf(okMessage.getBytes().length));
            request.response().write(okMessage);
            request.response().end();
        } else {
            request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
            String okMessage = String.valueOf(replyMessage.cause()) + "\n";
            request.response().putHeader("Content-Length", String.valueOf(okMessage.getBytes().length));
            request.response().write(okMessage);
            request.response().end();
        }
    }

}
