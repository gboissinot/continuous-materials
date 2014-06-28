package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.repoconnection;

import com.github.ebx.core.MessagingTemplate;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.ServiceAddressRegistry;
import fr.synchrotron.soleil.ica.proxy.utilities.HttpEndpointInfo;
import fr.synchrotron.soleil.ica.proxy.utilities.HttpServerRequestWrapper;
import fr.synchrotron.soleil.ica.proxy.utilities.PUTHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

/**
 * @author Gregory Boissinot
 */
public class PUTPOMHandler extends PUTHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PUTPOMHandler.class);

    public PUTPOMHandler(Vertx vertx, String contextPath, HttpEndpointInfo httpEndpointInfo) {
        super(vertx, contextPath, httpEndpointInfo);
    }

    @Override
    public void handle(final HttpServerRequestWrapper requestWrapper) {

        final HttpServerRequestWrapper.RequestTemplate requestTemplate = requestWrapper.clientTemplate();
        final String path = requestTemplate.getClientRequestPath();
        final HttpServerRequest request = requestWrapper.getRequest();

        System.out.println("Upload POM " + path);

        final HttpClient httpClient = requestWrapper.getHttpClient();

        final Buffer pomContentBuffer = new Buffer();

        final HttpClientRequest vertxHttpClientRequest = httpClient.put(path, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                final int statusCode = clientResponse.statusCode();
                request.response().setStatusCode(statusCode);
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                requestTemplate.fixWarningCookieDomain(clientResponse);
                clientResponse.endHandler(new Handler<Void>() {
                    public void handle(Void event) {
                        final AsyncResultHandler<Message<Void>> replyHandler = new AsyncResultHandler<Message<Void>>() {
                            @Override
                            public void handle(AsyncResult<Message<Void>> asyncResult) {
                                if (asyncResult.failed()) {
                                    asyncResult.cause().printStackTrace();
                                    LOG.error(asyncResult.cause().getMessage());
                                    request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                                    request.response().setStatusMessage(asyncResult.cause().getMessage());
                                }
                                request.response().end();
                                requestWrapper.closeHttpClient();
                            }
                        };
                        final JsonObject jsonObject = new JsonObject();
                        jsonObject.putString("action", "store");
                        jsonObject.putString("content", pomContentBuffer.toString());
                        MessagingTemplate
                                .address(vertx.eventBus(), ServiceAddressRegistry.EB_ADDRESS_POMMETADATA_SERVICE)
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
                requestTemplate.sendError(throwable);
                requestWrapper.closeHttpClient();
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
