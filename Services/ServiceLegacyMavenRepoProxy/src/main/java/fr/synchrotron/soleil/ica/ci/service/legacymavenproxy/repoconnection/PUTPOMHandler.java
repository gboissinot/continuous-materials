package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.repoconnection;

import com.github.ebx.core.MessagingTemplate;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.ServiceAddressRegistry;
import fr.synchrotron.soleil.ica.proxy.utilities.PUTHandler;
import fr.synchrotron.soleil.ica.proxy.utilities.ProxyService;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Gregory Boissinot
 */
public class PUTPOMHandler extends PUTHandler {

    public PUTPOMHandler(ProxyService proxyService) {
        super(proxyService);
    }

    @Override
    public void handleRequest(final HttpServerRequest request, final HttpClient vertxHttpClient) {

        final String path = proxyService.getRequestPath(request);
        System.out.println("Upload POM " + path);

        final Buffer pomContentBuffer = new Buffer();

        final HttpClientRequest vertxHttpClientRequest = vertxHttpClient.put(path, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                final int statusCode = clientResponse.statusCode();
                request.response().setStatusCode(statusCode);
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                proxyService.fixWarningCookieDomain(request, clientResponse);
                clientResponse.endHandler(new Handler<Void>() {
                    public void handle(Void event) {
                        final AsyncResultHandler<Message<Void>> replyHandler = new AsyncResultHandler<Message<Void>>() {
                            @Override
                            public void handle(AsyncResult<Message<Void>> asyncResult) {
                                if (asyncResult.failed()) {
                                    request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                                    request.response().setStatusMessage(asyncResult.cause().getMessage());
                                }
                                request.response().end();
                                vertxHttpClient.close();
                            }
                        };
                        final JsonObject jsonObject = new JsonObject();
                        jsonObject.putString("action", "store");
                        jsonObject.putString("content", pomContentBuffer.toString());
                        MessagingTemplate
                                .address(proxyService.getVertx().eventBus(), ServiceAddressRegistry.EB_ADDRESS_POMMETADATA_SERVICE)
                                .action("store")
                                .content(jsonObject).send(replyHandler);
                    }
                });
            }
        });

        vertxHttpClientRequest.headers().set(request.headers());
        vertxHttpClientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                proxyService.sendError(request, throwable);
                vertxHttpClient.close();
            }
        });

        request.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer data) {
                pomContentBuffer.appendBuffer(data);
                vertxHttpClientRequest.write(data);
            }
        });

        request.endHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                vertxHttpClientRequest.end();
            }
        });

    }

}
