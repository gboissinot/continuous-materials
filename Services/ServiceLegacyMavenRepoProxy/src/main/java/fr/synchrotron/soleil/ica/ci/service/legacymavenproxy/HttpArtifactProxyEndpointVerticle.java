package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.get.GETPOMHandler;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.get.GETPOMSha1Handler;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.put.PUTPOMHandler;
import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.GETHandler;
import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.PUTHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Gregory Boissinot
 */

public class HttpArtifactProxyEndpointVerticle extends BusModBase {

    private void populateGETRouteMatcher(RouteMatcher routeMatcher, String proxyPath) {
        final JsonObject getJsonObject = config.getObject("repo.get");
        final String repoHostGET = getJsonObject.getString("host");
        final int repoPortGET = getJsonObject.getInteger("port");
        final String repoURIPathGET = getJsonObject.getString("uri");

        routeMatcher
                .getWithRegEx(proxyPath + "/.*.pom", new GETPOMHandler(vertx, proxyPath, repoHostGET, repoPortGET, repoURIPathGET))
                .getWithRegEx(proxyPath + "/.*.pom.sha1", new GETPOMSha1Handler(vertx, proxyPath, repoHostGET, repoPortGET, repoURIPathGET))
                .getWithRegEx(proxyPath + "/.*", new GETHandler(vertx, proxyPath, repoHostGET, repoPortGET, repoURIPathGET));
    }

    private void populatePUTRouteMatcher(RouteMatcher routeMatcher, String proxyPath) {
        final JsonObject putJsonObject = config.getObject("repo.put");
        final String repoHostPUT = putJsonObject.getString("host");
        final int repoPortPUT = putJsonObject.getInteger("port");
        final String repoURIPathPUT = putJsonObject.getString("uri");

        routeMatcher
                .putWithRegEx(proxyPath + "/.*.pom", new PUTPOMHandler(vertx, proxyPath, repoHostPUT, repoPortPUT, repoURIPathPUT))
                .putWithRegEx(proxyPath + "/.*", new PUTHandler(vertx, proxyPath, repoHostPUT, repoPortPUT, repoURIPathPUT));
    }

    @Override
    public void start() {

        super.start();

        final int port = getMandatoryIntConfig("proxyPort");
        final String proxyPath = getMandatoryStringConfig("proxyPath");
        HttpServer httpServer = null;
        try {
            //--GET
            RouteMatcher routeMatcher = new RouteMatcher();
            populateGETRouteMatcher(routeMatcher, proxyPath);

            //--PUT
            populatePUTRouteMatcher(routeMatcher, proxyPath);

            //-- NO MATCH
            routeMatcher.noMatch(new Handler<HttpServerRequest>() {
                @Override
                public void handle(HttpServerRequest request) {
                    request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
                    request.response().end();
                }
            });

            httpServer = vertx.createHttpServer();
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

