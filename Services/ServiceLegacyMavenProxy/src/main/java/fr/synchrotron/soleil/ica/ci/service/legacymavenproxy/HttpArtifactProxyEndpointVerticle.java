package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pull.HttpArtifactPullHandler;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.push.HttpArtifactPushHandler;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */

public class HttpArtifactProxyEndpointVerticle extends Verticle {

    public static final String PROXY_PATH = "/legacyMavenProxy";

    @Override
    public void start() {

        super.start();

        final JsonObject config = container.config();

        final int port = config.getInteger("port");

        final JsonObject get = config.getObject("repo.get");
        final String repoHostGET = get.getString("repoHost");
        final int repoPortGET = get.getInteger("repoPort");
        final String repoURIPathGET = get.getString("repoURIPath");
        final HttpArtifactCaller httpArtifactCallerGET = new HttpArtifactCaller(vertx, repoHostGET, repoPortGET, repoURIPathGET);

        final JsonObject put = config.getObject("repo.put");
        final String repoHostPUT = put.getString("repoHost");
        final int repoPortPUT = put.getInteger("repoPort");
        final String repoURIPathPUT = put.getString("repoURIPath");
        final HttpArtifactCaller httpArtifactCallerPUT = new HttpArtifactCaller(vertx, repoHostPUT, repoPortPUT, repoURIPathPUT);

        HttpServer httpServer = null;
        try {

            httpServer = vertx.createHttpServer();

            final VertxDomainObject vertxDomainObject = new VertxDomainObject(vertx, container.logger());

            RouteMatcher routeMatcher = new RouteMatcher();
            routeMatcher.allWithRegEx(PROXY_PATH + "/.*", new Handler<HttpServerRequest>() {
                @Override
                public void handle(HttpServerRequest request) {
                    try {
                        final String method = request.method();
                        if (HttpMethod.GET.name().equals(method) || HttpMethod.HEAD.name().equals(method)) {

                            HttpArtifactPullHandler httpArtifactPullHandler =
                                    new HttpArtifactPullHandler(vertxDomainObject, httpArtifactCallerGET);
                            httpArtifactPullHandler.handle(request);
                        } else if (HttpMethod.PUT.name().equals(method)) {
                            HttpArtifactPushHandler httpArtifactPushHandler =
                                    new HttpArtifactPushHandler(vertxDomainObject, httpArtifactCallerPUT);
                            httpArtifactPushHandler.handle(request);
                        } else {
                            request.response().setStatusCode(HttpResponseStatus.METHOD_NOT_ALLOWED.code());
                            request.response().setStatusMessage(String.format("%s method is not supported.", method));
                            request.response().end();
                        }
                    } catch (Throwable e) {
                        request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                        request.response().setStatusMessage(e.toString());
                        request.response().end();
                    }
                }
            });
            httpServer.requestHandler(routeMatcher);
            httpServer.listen(port);

            container.logger().info("Webserver proxy started, listening on port:" + port);

        } catch (Throwable e) {
            container.logger().error(e);
            if (httpServer != null) {
                httpServer.close();
            }
        }
    }

}

