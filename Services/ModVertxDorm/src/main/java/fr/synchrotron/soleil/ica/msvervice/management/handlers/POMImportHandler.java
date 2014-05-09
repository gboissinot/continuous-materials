package fr.synchrotron.soleil.ica.msvervice.management.handlers;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class POMImportHandler implements Handler<HttpServerRequest> {

    private EventBus eventBus;
    private MessageUtilities messageUtilities;

    public POMImportHandler(EventBus eventBus) {
        if (eventBus == null) {
            throw new NullPointerException("A eventBus object is required.");
        }
        this.eventBus = eventBus;
        this.messageUtilities = new MessageUtilities(eventBus);
    }

    @Override
    public void handle(final HttpServerRequest request) {

        if (request == null) {
            throw new NullPointerException("A request object is required.");
        }

        final Buffer pomContent = new Buffer(0);
        request.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer buffer) {
                pomContent.appendBuffer(buffer);
            }
        });

        request.endHandler(new Handler<Void>() {
                               @Override
                               public void handle(Void event) {
                                   messageUtilities.sendReply("pom.importer", pomContent.toString(), request);
                               }
                           }
        );


    }
}
