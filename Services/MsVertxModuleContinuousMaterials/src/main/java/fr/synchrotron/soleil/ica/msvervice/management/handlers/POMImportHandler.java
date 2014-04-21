package fr.synchrotron.soleil.ica.msvervice.management.handlers;

import fr.synchrotron.soleil.ica.msvervice.management.HttpEndpointManager;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class POMImportHandler implements Handler<HttpServerRequest> {


    private EventBus eventBus;

    public POMImportHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final Buffer pomContent = new Buffer(0);

        request.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer buffer) {
                pomContent.appendBuffer(buffer);
            }
        });

        request.endHandler(
                new Handler<Void>() {
                    @Override
                    public void handle(Void event) {
                        eventBus.sendWithTimeout("pom.importer", pomContent, HttpEndpointManager.SEND_MS_TIMEOUT, new Handler<AsyncResult<Message<String>>>() {
                            @Override
                            public void handle(AsyncResult<Message<String>> replyMessage) {
                                if (replyMessage.succeeded()) {
                                    request.response().setStatusCode(200);
                                    request.response().setStatusMessage("OK.");
                                    String okMessage = String.valueOf(replyMessage.result().body()) + "\n";
                                    request.response().putHeader("Content-Length", String.valueOf(okMessage.getBytes().length));
                                    request.response().write(okMessage);
                                    request.response().end();
                                } else {
                                    request.response().setStatusCode(500);
                                    String okMessage = String.valueOf(replyMessage.cause()) + "\n";
                                    request.response().putHeader("Content-Length", String.valueOf(okMessage.getBytes().length));
                                    request.response().write(okMessage);
                                    request.response().end();
                                }
                            }
                        });
                    }
                }
        );


    }
}
