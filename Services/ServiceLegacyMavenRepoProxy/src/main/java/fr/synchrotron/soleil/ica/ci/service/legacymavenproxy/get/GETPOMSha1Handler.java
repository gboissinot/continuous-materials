package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.get;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.POMCache;
import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.GETHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class GETPOMSha1Handler extends GETHandler {

    public GETPOMSha1Handler(Vertx vertx, String proxyPath, String repoHost, int repoPort, String repoUri) {
        super(vertx, proxyPath, repoHost, repoPort, repoUri);
    }

    @Override
    public void handle(final HttpServerRequest request) {
        final String path = repositoryRequestBuilder.buildRequestPath(request);
        System.out.println("Download " + path);

        final POMCache pomCache = new POMCache();
        final String sha1 = pomCache.getSha1(vertx, path);
        if (sha1 != null) {
            request.response().setStatusCode(HttpResponseStatus.OK.code());
            request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(sha1.getBytes().length));
            request.response().end(sha1);
            return;
        }

        super.handle(request);
    }
}
