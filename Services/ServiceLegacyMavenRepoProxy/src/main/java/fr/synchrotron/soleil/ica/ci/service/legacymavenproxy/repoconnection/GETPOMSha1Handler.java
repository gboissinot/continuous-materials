package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.repoconnection;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pommetadata.POMCache;
import fr.synchrotron.soleil.ica.proxy.utilities.GETHandler;
import fr.synchrotron.soleil.ica.proxy.utilities.HttpEndpointInfo;
import fr.synchrotron.soleil.ica.proxy.utilities.HttpServerRequestWrapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class GETPOMSha1Handler extends GETHandler {

    public GETPOMSha1Handler(Vertx vertx, String contextPath, HttpEndpointInfo httpEndpointInfo) {
        super(vertx, contextPath, httpEndpointInfo);
    }

    @Override
    public void handle(final HttpServerRequestWrapper requestWrapper) {
        final HttpServerRequestWrapper.RequestTemplate requestTemplate = requestWrapper.clientTemplate();
        final String path = requestTemplate.getClientRequestPath();
        final POMCache pomCache = new POMCache(vertx);
        final String sha1 = pomCache.getSha1(path);
        if (sha1 != null) {
            final HttpServerRequest request = requestWrapper.getRequest();
            request.response().setStatusCode(HttpResponseStatus.OK.code());
            request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(sha1.getBytes().length));
            request.response().end(sha1);
            return;
        }
        super.handle(requestWrapper);
    }
}
