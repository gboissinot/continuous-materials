package fr.synchrotron.soleil.ica.proxy.utilities;

import com.github.ebx.core.MessageFilterService;
import com.github.ebx.core.MessagingTemplate;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.*;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.*;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.streams.Pump;

import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ProxyService {

    private final Vertx vertx;
    private final String proxyPath;
    private final String repoHost;
    private final int repoPort;
    private final String repoUri;

    private final HttpClient httpClient;

    public ProxyService(Vertx vertx,
                        String proxyPath,
                        String repoHost,
                        int repoPort,
                        String repoUri) {
        this.vertx = vertx;
        this.proxyPath = proxyPath;
        this.repoHost = repoHost;
        this.repoPort = repoPort;
        this.repoUri = repoUri;
        httpClient = vertx.createHttpClient()
                .setHost(repoHost)
                .setPort(repoPort)
                .setKeepAlive(false)
                .setMaxPoolSize(20);
    }

    public Vertx getVertx() {
        return vertx;
    }

    public HttpClient getVertxHttpClient() {
        return httpClient;
    }

    public void sendClientResponse(final HttpServerRequest request, HttpClientResponse clientResponse) {
        clientResponse.pause();

        request.response().setStatusCode(clientResponse.statusCode());
        request.response().setStatusMessage(clientResponse.statusMessage());
        request.response().headers().set(clientResponse.headers());
        //request.response().setChunked(true);
        fixWarningCookieDomain(request, clientResponse);
        clientResponse.endHandler(new Handler<Void>() {
            public void handle(Void event) {
                request.response().end();
                //httpClient.close();
            }
        });

        Pump.createPump(clientResponse, request.response()).start();

        clientResponse.resume();
    }

    private void sendClientResponseWithPayload(HttpServerRequest request, HttpClientResponse clientResponse, String messagePayload) {
        request.response().setStatusCode(clientResponse.statusCode());
        request.response().setStatusMessage(clientResponse.statusMessage());
        request.response().headers().set(clientResponse.headers());
        request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(messagePayload.getBytes().length));
        fixWarningCookieDomain(request, clientResponse);
        request.response().end(messagePayload);
    }

    public void sendError(HttpServerRequest request, Throwable throwable) {
        request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        throwable.printStackTrace();
        final String message = throwable.getMessage();
        if (message != null) {
            request.response().setStatusMessage(message);
        }
        request.response().end();
    }

    public void sendClientResponseWithFilters(final HttpServerRequest request,
                                              final HttpClientResponse clientResponse,
                                              final List<MessageFilterService> messageFilterServiceList) {

        final Buffer clientRepsonseBody = new Buffer();
        clientResponse.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(final Buffer data) {
                clientRepsonseBody.appendBuffer(data);
            }
        });

        clientResponse.endHandler(new VoidHandler() {
            @Override
            protected void handle() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.putString("requestPath", getRequestPath(request));
                jsonObject.putString("content", clientRepsonseBody.toString());
                applyFiltersAndRespond(vertx, request, clientResponse, messageFilterServiceList, jsonObject);
            }
        });
    }

    private void applyFiltersAndRespond(final Vertx vertx, final HttpServerRequest request,
                                        final HttpClientResponse clientResponse,
                                        final List<MessageFilterService> messageFilterServiceList,
                                        final JsonObject jsonObjectMessage) {

        final String messagePayload = jsonObjectMessage.getString("content");

        if (messageFilterServiceList.size() == 0) {
            sendClientResponseWithPayload(request, clientResponse, messagePayload);
            return;
        }

        final MessageFilterService messageFilterService = messageFilterServiceList.get(0);
        AsyncResultHandler<Message<String>> responseHandler = new AsyncResultHandler<Message<String>>() {
            @Override
            public void handle(AsyncResult<Message<String>> asyncResult) {
                if (asyncResult.succeeded()) {
                    messageFilterServiceList.remove(0);
                    jsonObjectMessage.putString("content", asyncResult.result().body());
                    applyFiltersAndRespond(vertx, request, clientResponse, messageFilterServiceList, jsonObjectMessage);
                } else {
                    sendError(request, asyncResult.cause());
                }
            }
        };
        MessagingTemplate
                .address(vertx.eventBus(), messageFilterService.getAddress())
                .action(messageFilterService.getAction())
                .content(jsonObjectMessage).send(responseHandler);
    }

    public void processGETRepositoryRequest(final HttpServerRequest request, Handler<HttpClientResponse> responseHandler) {

        HttpClient vertxHttpClient = getVertxHttpClient();
        final String path = getRequestPath(request);
        HttpClientRequest clientRequest = vertxHttpClient.get(path, responseHandler);
        clientRequest.headers().set(request.headers());
        clientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                sendError(request, throwable);
            }
        });
        clientRequest.end();
    }

    public void processHEADRepositoryRequest(final HttpServerRequest request, Handler<HttpClientResponse> responseHandler) {
        HttpClient vertxHttpClient = vertx.createHttpClient().setHost(repoHost).setPort(repoPort);
        final String path = getRequestPath(request);
        HttpClientRequest clientRequest = vertxHttpClient.head(path, responseHandler);
        clientRequest.headers().set(request.headers());
        clientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                sendError(request, throwable);
            }
        });
        clientRequest.end();
    }

    public void fixWarningCookieDomain(HttpServerRequest request, HttpClientResponse clientResponse) {
        final String setCookie = clientResponse.headers().get(HttpHeaders.SET_COOKIE);
        if (setCookie != null) {
            request.response().headers().set(HttpHeaders.SET_COOKIE, getNewCookieContent(setCookie));
        }
    }

    public String getRequestPath(final HttpServerRequest request) {
        String artifactPath = request.path().substring(proxyPath.length() + 1);
        return repoUri.endsWith("/") ? (repoUri + artifactPath) : (repoUri + "/" + artifactPath);
    }

    private String getNewCookieContent(String cookie) {
        int index = repoUri.indexOf("/", 1);
        if (index < 0)
            index = repoUri.length();
        return cookie.replace(repoUri.substring(0, index), proxyPath);
    }

}
