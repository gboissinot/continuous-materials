package fr.synchrotron.soleil.ica.proxy.utilities;

import com.github.ebx.core.MessageFilterService;
import com.github.ebx.core.MessagingTemplate;
import io.netty.handler.codec.http.HttpMethod;
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
public class HttpServerRequestWrapper {

    private final HttpServerRequest request;
    private final HttpClient httpClient;
    private final String contextPath;
    private final HttpEndpointInfo httpEndpointInfo;
    private final Vertx vertx;

    public HttpServerRequestWrapper(HttpServerRequest request, HttpClient httpClient, String contextPath, HttpEndpointInfo httpEndpointInfo, Vertx vertx) {
        this.request = request;
        this.httpClient = httpClient;
        this.contextPath = contextPath;
        this.httpEndpointInfo = httpEndpointInfo;
        this.vertx = vertx;
    }

    public void closeHttpClient() {
        this.httpClient.close();
    }

    public HttpServerRequest getRequest() {
        return request;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public RequestTemplate clientTemplate() {
        return new RequestTemplate();
    }

    public class RequestTemplate {

        public void headAndRespond() {
            makeHttpEndpointRequest(HttpMethod.HEAD, buildPassThroughResponseHandler());
        }

        public void getAndRespond() {
            makeHttpEndpointRequest(HttpMethod.GET, buildPassThroughResponseHandler());
        }

        public Handler<HttpClientResponse> buildPassThroughResponseHandler() {
            return new Handler<HttpClientResponse>() {
                @Override
                public void handle(HttpClientResponse clientResponse) {
                    clientResponse.pause();
                    request.response().setStatusCode(clientResponse.statusCode());
                    request.response().setStatusMessage(clientResponse.statusMessage());
                    request.response().headers().set(clientResponse.headers());
                    request.response().setChunked(true);
                    fixWarningCookieDomain(clientResponse);
                    clientResponse.endHandler(new Handler<Void>() {
                        public void handle(Void event) {
                            request.response().end();
                            closeHttpClient();
                        }
                    });
                    Pump.createPump(clientResponse, request.response()).start();
                    clientResponse.resume();
                }
            };
        }

        public void getAndRespond(final List<MessageFilterService> clientResponseFilters) {

            Handler<HttpClientResponse> responseHandler = new Handler<HttpClientResponse>() {
                @Override
                public void handle(final HttpClientResponse clientResponse) {
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
                            jsonObject.putString("requestPath", getClientRequestPath());
                            jsonObject.putString("content", clientRepsonseBody.toString());
                            applyClientResponseFiltersAndRespond(clientResponse, clientResponseFilters, jsonObject);
                            httpClient.close();
                        }
                    });
                }
            };

            makeHttpEndpointRequest(HttpMethod.GET, buildPassThroughResponseHandler());
        }

        private void makeHttpEndpointRequest(HttpMethod method, Handler<HttpClientResponse> responseHandler) {
            HttpClientRequest clientRequest;
            switch (method.name()) {
                case "HEAD":
                    clientRequest = httpClient.head(getClientRequestPath(), responseHandler);
                    break;

                case "GET":
                    clientRequest = httpClient.get(getClientRequestPath(), responseHandler);
                    break;

                case "PUT":
                    clientRequest = httpClient.put(getClientRequestPath(), responseHandler);
                    break;

                default:
                    throw new RuntimeException(String.format("%s not supported by the proxy.", method.name()));
            }

            clientRequest.headers().set(request.headers());
            clientRequest.exceptionHandler(new Handler<Throwable>() {
                @Override
                public void handle(Throwable throwable) {
                    sendError(throwable);
                    closeHttpClient();
                }
            });
            clientRequest.end();
        }

        private void applyClientResponseFiltersAndRespond(
                final HttpClientResponse clientResponse,
                final List<MessageFilterService> messageFilterServiceList,
                final JsonObject jsonObjectMessage) {

            final String messagePayload = jsonObjectMessage.getString("content");

            if (messageFilterServiceList.size() == 0) {
                request.response().setStatusCode(clientResponse.statusCode());
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(messagePayload.getBytes().length));
                fixWarningCookieDomain(clientResponse);
                request.response().end(messagePayload);
                return;
            }

            final MessageFilterService messageFilterService = messageFilterServiceList.get(0);
            AsyncResultHandler<Message<String>> responseHandler = new AsyncResultHandler<Message<String>>() {
                @Override
                public void handle(AsyncResult<Message<String>> asyncResult) {
                    if (asyncResult.succeeded()) {
                        messageFilterServiceList.remove(0);
                        jsonObjectMessage.putString("content", asyncResult.result().body());
                        applyClientResponseFiltersAndRespond(clientResponse, messageFilterServiceList, jsonObjectMessage);
                    } else {
                        sendError(asyncResult.cause());
                    }
                }
            };
            MessagingTemplate
                    .address(vertx.eventBus(), messageFilterService.getAddress())
                    .action(messageFilterService.getAction())
                    .content(jsonObjectMessage).send(responseHandler);
        }

        public void sendError(Throwable throwable) {
            request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
            throwable.printStackTrace();
            final String message = throwable.getMessage();
            if (message != null) {
                request.response().setStatusMessage(message);
            }
            request.response().end();
        }

        public void fixWarningCookieDomain(HttpClientResponse clientResponse) {
            final String setCookie = clientResponse.headers().get(HttpHeaders.SET_COOKIE);
            if (setCookie != null) {
                request.response().headers().set(HttpHeaders.SET_COOKIE, getNewCookieContent(setCookie));
            }
        }

        public String getClientRequestPath() {
            String artifactPath = request.path().substring(contextPath.length() + 1);
            String repoUri = httpEndpointInfo.getUri();
            return repoUri.endsWith("/") ? (repoUri + artifactPath) : (repoUri + "/" + artifactPath);
        }

        private String getNewCookieContent(String cookie) {
            String repoUri = httpEndpointInfo.getUri();
            int index = repoUri.indexOf("/", 1);
            if (index < 0)
                index = repoUri.length();
            return cookie.replace(repoUri.substring(0, index), contextPath);
        }
    }

}
