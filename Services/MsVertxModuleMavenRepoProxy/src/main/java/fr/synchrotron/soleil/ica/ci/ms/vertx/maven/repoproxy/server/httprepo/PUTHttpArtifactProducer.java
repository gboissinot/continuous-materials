package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.server.httprepo;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpServerRequest;

import java.util.Map;

/**
 * @author Gregory Boissinot
 */
@Component
@Scope("singleton")
@Profile("repoHttp")
public class PUTHttpArtifactProducer extends HttpArtifactProducer {

    public void handle(final HttpServerRequest request) {

        final HttpClient pClient = getPClient();
        final String path = buildRequestPath(request);
        logger.info("Uploading to " + path);

        final HttpClientRequest clientRequest1 = pClient.put(path, new HandleResponseClient(request));
        final MultiMap headers = request.headers();
        for (Map.Entry<String, String> headerEntry : headers) {
            final String key = headerEntry.getKey();
            if ("Authorization".equalsIgnoreCase(key)) {
                clientRequest1.putHeader(key, headerEntry.getValue());
            }
        }

        request.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer event) {
                clientRequest1.putHeader("Content-Length", String.valueOf(event.getBytes().length));
                clientRequest1.write(event);
            }
        });

        request.endHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                clientRequest1.end();
            }
        });

    }
}