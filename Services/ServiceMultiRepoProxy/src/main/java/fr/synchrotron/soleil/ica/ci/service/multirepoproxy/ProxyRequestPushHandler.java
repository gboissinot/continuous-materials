package fr.synchrotron.soleil.ica.ci.service.multirepoproxy;

import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.RepositoryRequestBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.*;
import org.vertx.java.core.streams.Pump;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gregory Boissinot
 */
public class ProxyRequestPushHandler implements Handler<HttpServerRequest> {

    private Vertx vertx;
    private RepositoryObject repositoryInfo;
    protected RepositoryRequestBuilder repositoryRequestBuilder;

    public ProxyRequestPushHandler(Vertx vertx, RepositoryObject repositoryInfo) {
        this.vertx = vertx;
        this.repositoryInfo = repositoryInfo;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final HttpClient vertxHttpClient = vertx.createHttpClient();

        final String path = repositoryRequestBuilder.buildRequestPath(request);
        System.out.println("Upload " + path);

        request.pause();
        final HttpClientRequest vertxHttpClientRequest = vertxHttpClient.put(path, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                request.response().setStatusCode(clientResponse.statusCode());
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                clientResponse.endHandler(new Handler<Void>() {
                    public void handle(Void event) {
                        request.response().end();
                    }
                });
            }
        });

        vertxHttpClientRequest.headers().set(request.headers());
        vertxHttpClientRequest.setChunked(true);
        vertxHttpClientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("Exception from ").append(repositoryRequestBuilder.getRepoHost());
                errorMsg.append("-->").append(throwable.toString());
                errorMsg.append("\n");
                request.response().end(errorMsg.toString());
            }
        });

        request.endHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                vertxHttpClientRequest.end();
            }
        });

        final Pump pump = Pump.createPump(request, vertxHttpClientRequest);
        pump.start();
        request.resume();
//        final HttpClient vertxHttpClient = vertx.createHttpClient();
//        vertxHttpClient.setHost(repositoryInfo.getHost()).setPort(repositoryInfo.getPort());
//
//        final HttpClientRequest clientRequest = vertxHttpClient.put(repositoryInfo.getUri(), new Handler<HttpClientResponse>() {
//            @Override
//            public void handle(HttpClientResponse clientResponse) {
//                final int statusCode = clientResponse.statusCode();
//                request.response().setStatusCode(statusCode);
//                request.response().setStatusMessage(clientResponse.statusMessage());
//                request.response().headers().set(clientResponse.headers());
//                clientResponse.endHandler(new Handler<Void>() {
//                    public void handle(Void event) {
//                        request.response().end();
//                    }
//                });
//            }
//            //}
//        });
//
//        final MultiMap headers = request.headers();
//        for (Map.Entry<String, String> headerEntry : headers) {
//            final String key = headerEntry.getKey();
//            if ("Authorization".equalsIgnoreCase(key)) {
//                clientRequest.putHeader(key, headerEntry.getValue());
//            }
//        }
//
//        final String contentLengthHeader = headers.get(HttpHeaders.CONTENT_LENGTH);
//        if (contentLengthHeader != null) {
//            clientRequest.putHeader(HttpHeaders.CONTENT_LENGTH, contentLengthHeader);
//        }
//
//        request.dataHandler(new Handler<Buffer>() {
//            @Override
//            public void handle(Buffer data) {
//                clientRequest.write(data);
//                int requestId = request.path().hashCode();
//                StringBuilder content = pomStorage.get(requestId);
//                if (content == null) {
//                    pomStorage.put(requestId, new StringBuilder(data.toString()));
//                } else {
//                    pomStorage.put(requestId, content.append(data.toString()));
//                }
//            }
//        });

    }
}
