package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */
public class DORMProxyEndpointVerticle extends Verticle {

    public static final String PROXY_PATH = "/dormproxy";

    @Override
    public void start() {
        final HttpServer httpServer = vertx.createHttpServer();
        RouteMatcher routeMatcher = new RouteMatcher();
        routeMatcher.putWithRegEx(PROXY_PATH + "/.*", new ProxyRequestHandler(vertx));

        routeMatcher.allWithRegEx(PROXY_PATH + "/.*", new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest request) {
                request.response().setStatusCode(HttpResponseStatus.METHOD_NOT_ALLOWED.code());
                request.response().end();
            }
        });

        routeMatcher.noMatch(new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest request) {
                request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
                request.response().end();
            }
        });

        httpServer.requestHandler(routeMatcher);
        httpServer.listen(8080);

        container.logger().info("Webserver proxy started, listening on port:" + 8080);

    }
}
