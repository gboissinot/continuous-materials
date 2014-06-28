package fr.synchrotron.soleil.ica.proxy.utilities;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public abstract class HandlerHttpServerRequest implements Handler<HttpServerRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(HandlerHttpServerRequest.class);

    protected Vertx vertx;
    private String contextPath;
    private HttpEndpointInfo httpEndpointInfo;

    protected HandlerHttpServerRequest(Vertx vertx, String contextPath, HttpEndpointInfo httpEndpointInfo) {
        this.vertx = vertx;
        this.contextPath = contextPath;
        this.httpEndpointInfo = httpEndpointInfo;
    }

    @Override
    public void handle(final HttpServerRequest request) {
        try {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Incoming client request : " + request.method() + " " + request.uri());
            }

            //We have here a particular situation (reverse proxy).
            //We create an HttpClient object for each server request
            //It is supposed to be lightweight
            final HttpClient httpClient = vertx.createHttpClient()
                    .setHost(httpEndpointInfo.getHost())
                    .setPort(httpEndpointInfo.getPort())
                    .setKeepAlive(false);

            //Sample use case: timeout on client request
            request.exceptionHandler(new Handler<Throwable>() {
                @Override
                public void handle(Throwable t) {
                    LOG.error("Severe error during request processing :", t);
                    request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                    request.response().end();
                    httpClient.close();
                }
            });

            HttpServerRequestWrapper requestWrapper = new HttpServerRequestWrapper(request, httpClient, contextPath, httpEndpointInfo, vertx);
            cleanRequestHttpHeaders(request);
            handle(requestWrapper);

        } catch (Throwable t) {
            LOG.error("The routeMatcher throw an error", t);
            LOG.error("Severe error during request processing :", t);
            request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
            request.response().end();
        }
    }

    private void cleanRequestHttpHeaders(HttpServerRequest request) {
        final MultiMap headers = request.headers();
        for (Map.Entry<String, String> header : headers) {
            String headerValue = header.getValue();
            if (headerValue == null) {
                headers.remove(header.getKey());
            }
        }
        headers.remove(HttpHeaders.KEEP_ALIVE);
        headers.remove(HttpHeaders.CONNECTION);  //not necessary with keepAlive to false from clients
    }

    public abstract void handle(final HttpServerRequestWrapper request);

}
