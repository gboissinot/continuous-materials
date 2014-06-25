package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.repoconnection.GETPOMHandler;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.repoconnection.GETPOMSha1Handler;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.repoconnection.PUTPOMHandler;
import fr.synchrotron.soleil.ica.proxy.utilities.GETHandler;
import fr.synchrotron.soleil.ica.proxy.utilities.HttpClientProxy;
import fr.synchrotron.soleil.ica.proxy.utilities.PUTHandler;
import fr.synchrotron.soleil.ica.proxy.utilities.RequestHandlerWrapper;
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

    @Override
    public void start() {

        super.start();

        final int port = getMandatoryIntConfig("proxyPort");
        final String proxyPath = getMandatoryStringConfig("proxyPath");
        HttpServer httpServer = null;
        try {
            RouteMatcher routeMatcher = new RouteMatcher();

            //--HEAD
            final JsonObject getJsonObject = config.getObject("repo.get");
            final String repoHostGET = getJsonObject.getString("host");
            final int repoPortGET = getJsonObject.getInteger("port");
            final String repoURIPathGET = getJsonObject.getString("uri");
            routeMatcher.headWithRegEx(proxyPath + "/.*", new HEADHandler(new HttpClientProxy(vertx, proxyPath, repoHostGET, repoPortGET, repoURIPathGET)));

            //--GET

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
            httpServer.requestHandler(new RequestHandlerWrapper(routeMatcher));
            httpServer.listen(port);

            container.logger().info("Webserver proxy started, listening on port:" + port);

        } catch (Throwable e) {
            container.logger().error(e);
            if (httpServer != null) {
                httpServer.close();
            }
        }
    }

    private void populateGETRouteMatcher(RouteMatcher routeMatcher, String proxyPath) {
        final JsonObject getJsonObject = config.getObject("repo.get");
        final String repoHostGET = getJsonObject.getString("host");
        final int repoPortGET = getJsonObject.getInteger("port");
        final String repoURIPathGET = getJsonObject.getString("uri");

        final HttpClientProxy httpClientProxy = new HttpClientProxy(vertx, proxyPath, repoHostGET, repoPortGET, repoURIPathGET);
        routeMatcher
                .getWithRegEx(proxyPath + "/.*.pom", new GETPOMHandler(httpClientProxy))
                .getWithRegEx(proxyPath + "/.*.pom.sha1", new GETPOMSha1Handler(httpClientProxy))
                .getWithRegEx(proxyPath + "/.*", new GETHandler(httpClientProxy));
    }

    private void populatePUTRouteMatcher(RouteMatcher routeMatcher, String proxyPath) {
        final JsonObject putJsonObject = config.getObject("repo.put");
        final String repoHostPUT = putJsonObject.getString("host");
        final int repoPortPUT = putJsonObject.getInteger("port");
        final String repoURIPathPUT = putJsonObject.getString("uri");

        final HttpClientProxy httpClientProxy = new HttpClientProxy(vertx, proxyPath, repoHostPUT, repoPortPUT, repoURIPathPUT);
        routeMatcher
                .putWithRegEx(proxyPath + "/.*.pom", new PUTPOMHandler(httpClientProxy))
                .putWithRegEx(proxyPath + "/.*", new PUTHandler(httpClientProxy));
    }

}