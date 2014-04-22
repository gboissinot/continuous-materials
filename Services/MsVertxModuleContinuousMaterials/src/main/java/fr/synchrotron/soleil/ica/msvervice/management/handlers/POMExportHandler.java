package fr.synchrotron.soleil.ica.msvervice.management.handlers;

import fr.synchrotron.soleil.ica.msvervice.management.HttpEndpointManager;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

import java.util.Set;

/**
 * @author Gregory Boissinot
 */
public class POMExportHandler extends AbstractHandler {

    private EventBus eventBus;

    public POMExportHandler(EventBus eventBus) {
        if (eventBus == null) {
            throw new NullPointerException("A eventBus object is required.");
        }
        this.eventBus = eventBus;
    }


    @Override
    public void handle(final HttpServerRequest request) {

        if (request == null) {
            throw new NullPointerException("A request object is required.");
        }

        final JsonObject pomIdObject = new JsonObject();

        request.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer data) {

                final JsonObject jsonObject = new JsonObject(data.toString());
                final Set<String> fieldNames = jsonObject.getFieldNames();
                for (String fieldName : fieldNames) {
                    pomIdObject.putString(fieldName, jsonObject.getString(fieldName));
                }
            }
        });

        request.endHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                eventBus.sendWithTimeout("pom.exporter", pomIdObject, HttpEndpointManager.SEND_MS_TIMEOUT, new Handler<AsyncResult<Message<String>>>() {
                    @Override
                    public void handle(AsyncResult<Message<String>> replyMessage) {
                        buildStringReplyMessage(replyMessage, request);
                    }
                });
            }
        });


    }
}
