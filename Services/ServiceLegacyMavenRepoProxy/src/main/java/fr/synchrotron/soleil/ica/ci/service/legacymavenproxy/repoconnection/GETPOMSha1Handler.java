package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.repoconnection;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pommetadata.POMCache;
import fr.synchrotron.soleil.ica.proxy.utilities.GETHandler;
import fr.synchrotron.soleil.ica.proxy.utilities.HttpClientProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class GETPOMSha1Handler extends GETHandler {

    public GETPOMSha1Handler(HttpClientProxy httpClientProxy) {
        super(httpClientProxy);
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final String path = httpClientProxy.getRequestPath(request);
        final POMCache pomCache = new POMCache(httpClientProxy.getVertx());
        final String sha1 = pomCache.getSha1(path);
        if (sha1 != null) {
            System.out.println("Using stored sha1");
            request.response().setStatusCode(HttpResponseStatus.OK.code());
            request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(sha1.getBytes().length));
            request.response().end(sha1);
            return;
        }

        super.handle(request);
    }
}
