package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.repoconnection;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pommetadata.POMCache;
import fr.synchrotron.soleil.ica.proxy.utilities.GETHandler;
import fr.synchrotron.soleil.ica.proxy.utilities.ProxyService;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class GETPOMSha1Handler extends GETHandler {

    public GETPOMSha1Handler(ProxyService proxyService) {
        super(proxyService);
    }

    @Override
    public void handle(final HttpServerRequest request) {
        final String path = proxyService.getRequestPath(request);
        final POMCache pomCache = new POMCache(proxyService.getVertx());
        final String sha1 = pomCache.getSha1(path);
        if (sha1 != null) {
            request.response().setStatusCode(HttpResponseStatus.OK.code());
            request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(sha1.getBytes().length));
            request.response().end(sha1);
            return;
        }
        super.handle(request);
    }
}
