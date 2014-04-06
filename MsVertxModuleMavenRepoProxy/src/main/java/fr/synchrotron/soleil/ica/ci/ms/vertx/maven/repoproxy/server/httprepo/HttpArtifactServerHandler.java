package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.server.httprepo;

import fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.server.httprepo.get.GETHttpArtifactProducer;
import fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.server.httprepo.put.PUTHttpArtifactProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
@Component
@Scope("singleton")
@Profile("repoHttp")
public class HttpArtifactServerHandler implements Handler<HttpServerRequest> {

    @Autowired
    private GETHttpArtifactProducer getHttpArtifactProducer;

    @Autowired
    private PUTHttpArtifactProducer putHttpArtifactProducer;

    private Vertx vertx;

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(final HttpServerRequest request) {
        try {

            final String method = request.method();
            if ("GET".equals(method) || "HEAD".equals(method)) {
                getHttpArtifactProducer.setVertx(vertx);
                getHttpArtifactProducer.handle(request);
            } else if ("PUT".equals(method)) {
                putHttpArtifactProducer.setVertx(vertx);
                putHttpArtifactProducer.handle(request);
            } else {
                request.response().setStatusCode(400);
                request.response().setStatusMessage("Only GET requests are supported for now.");
                request.response().end();
            }

        } catch (Throwable e) {
            request.response().setStatusCode(500);
            request.response().setStatusMessage(e.toString());
            request.response().end();
        }
    }

}
