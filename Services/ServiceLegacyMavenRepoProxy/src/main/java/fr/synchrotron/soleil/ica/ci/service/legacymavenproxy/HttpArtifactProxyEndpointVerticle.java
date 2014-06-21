package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.repoconnection.GETPOMHandler;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.repoconnection.GETPOMSha1Handler;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.repoconnection.PUTPOMHandler;
import fr.synchrotron.soleil.ica.proxy.utilities.GETHandler;
import fr.synchrotron.soleil.ica.proxy.utilities.HttpClientProxy;
import fr.synchrotron.soleil.ica.proxy.utilities.PUTHandler;
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

        final JsonObject getJsonObject = config.getObject("repository");
        final String repositoryHost = getJsonObject.getString("host");
        final int repositoryPort = getJsonObject.getInteger("port");
        final String repositoryURI = getJsonObject.getString("uri");

        HttpServer httpServer = null;
        try {
            RouteMatcher routeMatcher = new RouteMatcher();

            //--GET
            routeMatcher
                    .getWithRegEx(proxyPath + "/.*.pom", new GETPOMHandler(new HttpClientProxy(vertx, proxyPath, repositoryHost, repositoryPort, repositoryURI)))
                    .getWithRegEx(proxyPath + "/.*.pom.sha1", new GETPOMSha1Handler(new HttpClientProxy(vertx, proxyPath, repositoryHost, repositoryPort, repositoryURI)))
                    .getWithRegEx(proxyPath + "/.*", new GETHandler(new HttpClientProxy(vertx, proxyPath, repositoryHost, repositoryPort, repositoryURI)));

            //--PUT
            routeMatcher
                    .putWithRegEx(proxyPath + "/.*.pom", new PUTPOMHandler(new HttpClientProxy(vertx, proxyPath, repositoryHost, repositoryPort, repositoryURI)))
                    .putWithRegEx(proxyPath + "/.*", new PUTHandler(new HttpClientProxy(vertx, proxyPath, repositoryHost, repositoryPort, repositoryURI)));


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

