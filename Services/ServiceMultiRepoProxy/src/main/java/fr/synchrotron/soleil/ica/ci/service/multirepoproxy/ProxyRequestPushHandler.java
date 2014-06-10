package fr.synchrotron.soleil.ica.ci.service.multirepoproxy;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gregory Boissinot
 */
public class ProxyRequestPushHandler implements Handler<HttpServerRequest> {

    private Vertx vertx;
    private RepositoryObject repositoryInfo;
    private Map<Integer, StringBuilder> pomStorage = new ConcurrentHashMap<Integer, StringBuilder>();

    public ProxyRequestPushHandler(Vertx vertx, RepositoryObject repositoryInfo) {
        this.vertx = vertx;
        this.repositoryInfo = repositoryInfo;
    }

    public void handle(final HttpServerRequest request) {

        final HttpClient vertxHttpClient = vertx.createHttpClient();
        vertxHttpClient.setHost(repositoryInfo.getHost()).setPort(repositoryInfo.getPort());

        final HttpClientRequest clientRequest = vertxHttpClient.put(repositoryInfo.getUri(), new Handler<HttpClientResponse>() {
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
            }
            //}
        });

        final MultiMap headers = request.headers();
        for (Map.Entry<String, String> headerEntry : headers) {
            final String key = headerEntry.getKey();
            if ("Authorization".equalsIgnoreCase(key)) {
                clientRequest.putHeader(key, headerEntry.getValue());
            }
        }

        final String contentLengthHeader = headers.get(HttpHeaders.CONTENT_LENGTH);
        if (contentLengthHeader != null) {
            clientRequest.putHeader(HttpHeaders.CONTENT_LENGTH, contentLengthHeader);
        }

        request.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer data) {
                clientRequest.write(data);
                int requestId = request.path().hashCode();
                StringBuilder content = pomStorage.get(requestId);
                if (content == null) {
                    pomStorage.put(requestId, new StringBuilder(data.toString()));
                } else {
                    pomStorage.put(requestId, content.append(data.toString()));
                }
            }
        });

    }
}
