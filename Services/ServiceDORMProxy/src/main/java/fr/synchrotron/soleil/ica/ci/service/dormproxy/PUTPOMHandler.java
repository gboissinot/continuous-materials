package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.*;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;


/**
 * @author Gregory Boissinot
 */
public class PUTPOMHandler implements Handler<HttpServerRequest> {

    private Vertx vertx;

    public PUTPOMHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final Buffer body = new Buffer();
        request.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer data) {
                body.appendBuffer(data);
            }
        });

        request.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                request.response().end();
            }
        });

        request.endHandler(new VoidHandler() {
            @Override
            protected void handle() {
                vertx.eventBus().sendWithTimeout(
                        ServiceAddressRegistry.EB_ADDRESS_POMIMPORT_SERVICE,
                        body.toString(), Integer.MAX_VALUE, new AsyncResultHandler<Message<Boolean>>() {
                            @Override
                            public void handle(AsyncResult<Message<Boolean>> asyncResult) {
                                if (asyncResult.succeeded()) {
                                    request.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code());
                                    request.response().end();
                                } else {
                                    request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                                    //request.response().setStatusMessage(asyncResult.cause().getMessage());
                                    request.response().end();
                                }

                            }
                        }
                );

            }
        });
    }
}
