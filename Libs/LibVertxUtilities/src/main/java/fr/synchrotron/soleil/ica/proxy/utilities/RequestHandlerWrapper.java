package fr.synchrotron.soleil.ica.proxy.utilities;

import org.apache.commons.httpclient.HttpStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

/**
 * @author Gregory Boissinot
 */
public class RequestHandlerWrapper implements Handler<HttpServerRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandlerWrapper.class);
    private final RouteMatcher routeMatcher;

    public RequestHandlerWrapper(RouteMatcher routeMatcher) {
        this.routeMatcher = routeMatcher;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Incoming request : " + request.method() + " " + request.uri());
        }

        request.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable t) {
                LOG.error("Severe error during request processing :", t);
                request.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                request.response().end();
            }
        });

        try {
            routeMatcher.handle(request);
        } catch (Throwable t) {
            LOG.error("The routeMatcher throw an error", t);
            LOG.error("Severe error during request processing :", t);
            request.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            request.response().end();
        }
    }

}
