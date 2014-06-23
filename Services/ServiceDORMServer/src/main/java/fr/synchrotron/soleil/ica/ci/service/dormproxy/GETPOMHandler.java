package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import com.github.ebx.core.MessagingTemplate;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Gregory Boissinot
 */
public class GETPOMHandler implements Handler<HttpServerRequest> {

    private Vertx vertx;

    public GETPOMHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final String path = request.path();
        System.out.println("GET" + path);

        JsonObject pomQuery = new JsonObject();
        //TODO extract query parameters to build pomQuery
        pomQuery.putString("org", "test");
        pomQuery.putString("name", "test");
        pomQuery.putString("version", "1.0");
        pomQuery.putString("status", "RELEASE");
        MessagingTemplate.address(vertx.eventBus(), ServiceAddressRegistry.EB_ADDRESS_POMIMPORT_SERVICE)
                .content(pomQuery)
                .action("export").send(new AsyncResultHandler<Message<String>>() {
            @Override
            public void handle(AsyncResult<Message<String>> asyncResult) {
                if (asyncResult.succeeded()) {
                    String pomContent = asyncResult.result().body();
                    request.response().setStatusCode(HttpResponseStatus.OK.code());
                    request.response().end(pomContent);
                } else {
                    request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                    request.response().end();
                }
            }
        });
    }
}
