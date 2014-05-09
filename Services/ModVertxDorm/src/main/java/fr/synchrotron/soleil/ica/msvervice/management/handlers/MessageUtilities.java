package fr.synchrotron.soleil.ica.msvervice.management.handlers;

import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.ActionMessageManagement;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Gregory Boissinot
 */
public class MessageUtilities {

    public static final long SEND_MS_TIMEOUT = 10 * 1000l; // in ms

    private EventBus eventBus;

    public MessageUtilities(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void sendReply(String address, Object documentContent, final HttpServerRequest request) {
        ActionMessageManagement actionMessageManagement = new ActionMessageManagement();
        final JsonObject sendMessage = actionMessageManagement.createMessage(address, documentContent);
        eventBus.sendWithTimeout(address, sendMessage, SEND_MS_TIMEOUT, new Handler<AsyncResult<Message<String>>>() {
            @Override
            public void handle(AsyncResult<Message<String>> replyMessage) {
                buildStringReplyMessage(replyMessage, request);
            }
        });
    }

    public void buildStringReplyMessage(final AsyncResult<Message<String>> replyMessage, final HttpServerRequest request) {
        if (replyMessage.succeeded()) {
            request.response().setStatusCode(HttpResponseStatus.OK.code());
            request.response().setStatusMessage(HttpResponseStatus.OK.reasonPhrase());
            String okMessage = String.valueOf(replyMessage.result().body()) + "\n";
            request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(okMessage.getBytes().length));
            request.response().write(okMessage);
            request.response().end();
        } else {
            request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
            String okMessage = String.valueOf(replyMessage.cause()) + "\n";
            request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(okMessage.getBytes().length));
            request.response().write(okMessage);
            request.response().end();
        }
    }

}
