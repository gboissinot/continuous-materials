package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pull;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.HttpArtifactCaller;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.VertxDomainObject;
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
import org.vertx.java.core.streams.Pump;

/**
 * @author Gregory Boissinot
 */
public class HttpArtifactPullHandler {

    private static final String SUFFIX_POM = "pom";

    private final VertxDomainObject vertxDomainObject;
    private final HttpArtifactCaller httpArtifactCaller;

    public HttpArtifactPullHandler(VertxDomainObject vertxDomainObject, HttpArtifactCaller httpArtifactCaller) {
        this.vertxDomainObject = vertxDomainObject;
        this.httpArtifactCaller = httpArtifactCaller;
    }

    public void handle(final HttpServerRequest request) {

        final HttpClient pClient = httpArtifactCaller.getPClient();
        final String path = httpArtifactCaller.buildRequestPath(request);
        vertxDomainObject.getLogger().info("[Vert.x] - Requesting to download " + path);

        HttpClientRequest clientRequest;

        if (!path.endsWith(SUFFIX_POM)) {
            clientRequest = pClient.get(path, new Handler<HttpClientResponse>() {
                @Override
                public void handle(HttpClientResponse clientResponse) {
                    final int statusCode = clientResponse.statusCode();
                    request.response().setStatusCode(statusCode);
                    request.response().setStatusMessage(clientResponse.statusMessage());
                    request.response().headers().set(clientResponse.headers());
                    clientResponse.endHandler(new Handler<Void>() {
                        public void handle(Void event) {
                            request.response().end();
                        }
                    });
                    if (statusCode == HttpResponseStatus.NOT_MODIFIED.code()
                            || statusCode == HttpResponseStatus.OK.code()) {
                        //Send result to original client
                        Pump.createPump(clientResponse, request.response().setChunked(true)).start();
                    }
                }
            });
        } else {
            clientRequest = pClient.get(path, new Handler<HttpClientResponse>() {
                @Override
                public void handle(HttpClientResponse clientResponse) {

                    int statusCode = clientResponse.statusCode();

                    if (statusCode == HttpResponseStatus.NOT_FOUND.code()) {
                        request.response().setStatusCode(statusCode);
                        request.response().end();
                        return;
                    }

                    clientResponse.bodyHandler(new Handler<Buffer>() {
                        @Override
                        public void handle(Buffer data) {
                            vertxDomainObject.getVertx().eventBus().sendWithTimeout("pom.fix", data.toString(), Integer.MAX_VALUE, new AsyncResultHandler<Message<String>>() {
                                @Override
                                public void handle(AsyncResult<Message<String>> asyncResult) {
                                    if (asyncResult.succeeded()) {
                                        final Message<String> pomResultMessage = asyncResult.result();
                                        final String pomResultContent = pomResultMessage.body();
                                        request.response().putHeader("Content-Length", String.valueOf(pomResultContent.getBytes().length));
                                        request.response().end(pomResultContent);
                                    } else {
                                        request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                                        request.response().end(asyncResult.cause().toString());
                                    }

                                }
                            });
                        }
                    });
                }
            });
        }

        //clientRequest.setTimeout(1);
        clientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable e) {
                request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("Exception from ").append(httpArtifactCaller.getRepoHost());
                errorMsg.append("-->").append(e.toString());
                errorMsg.append("\n");
                request.response().end(errorMsg.toString());
            }
        });

        clientRequest.end();

    }

}
