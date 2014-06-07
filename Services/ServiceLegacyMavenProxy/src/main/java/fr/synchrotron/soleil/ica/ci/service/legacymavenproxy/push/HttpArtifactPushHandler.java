package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.push;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.HttpArtifactCaller;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.ServiceAddressRegistry;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.VertxDomainObject;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gregory Boissinot
 */
public class HttpArtifactPushHandler {

    private final VertxDomainObject vertxDomainObject;
    private final HttpArtifactCaller httpArtifactCaller;
    private ConcurrentHashMap<Integer, StringBuilder> pomStorage = new ConcurrentHashMap<Integer, StringBuilder>();

    public HttpArtifactPushHandler(VertxDomainObject vertxDomainObject, HttpArtifactCaller httpArtifactCaller) {
        this.vertxDomainObject = vertxDomainObject;
        this.httpArtifactCaller = httpArtifactCaller;
    }

    public void handle(final HttpServerRequest request) {

        final HttpClient pClient = httpArtifactCaller.getPClient();
        final String path = httpArtifactCaller.buildRequestPath(request);
        vertxDomainObject.getLogger().info("Uploading to " + path);

        final HttpClientRequest clientRequest = pClient.put(path, new Handler<HttpClientResponse>() {
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

        request.endHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                if (path.endsWith(".pom")) {
                    int code = request.path().hashCode();
                    StringBuilder content = pomStorage.get(code);
                    vertxDomainObject.getVertx().eventBus().send(ServiceAddressRegistry.EB_ADDRESS_TRACK_POM, content.toString());
                }
            }
        });
    }
}