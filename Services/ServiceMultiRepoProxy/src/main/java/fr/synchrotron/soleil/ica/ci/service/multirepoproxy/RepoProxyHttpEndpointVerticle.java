package fr.synchrotron.soleil.ica.ci.service.multirepoproxy;

import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.PUTHandler;
import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.RepositoryObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class RepoProxyHttpEndpointVerticle extends Verticle {

    @Override
    public void start() {

        final JsonObject config = container.config();
        final int port = config.getInteger("proxyPort");
        final String proxyPath = config.getString("proxyPath");

        //GET
        final JsonArray repositories = config.getArray("repositories.get");
        final List<RepositoryObject> repos = buildRepoUrls(repositories);
        RouteMatcher routeMatcher = new RouteMatcher();
        routeMatcher.headWithRegEx(proxyPath + "/.*", new MutltiGETHandler(vertx, proxyPath, repos));
        routeMatcher.getWithRegEx(proxyPath + "/.*", new MutltiGETHandler(vertx, proxyPath, repos));

        //PUT
        final JsonObject putJsonObject = config.getObject("repo.put");
        final String repoHostPUT = putJsonObject.getString("host");
        final int repoPortPUT = putJsonObject.getInteger("port");
        final String repoURIPathPUT = putJsonObject.getString("uri");
        routeMatcher.putWithRegEx(proxyPath + "/.*", new PUTHandler(vertx, proxyPath, repoHostPUT, repoPortPUT, repoURIPathPUT));


        //Other than HEAD, GET or PUT are not supported
        routeMatcher.allWithRegEx(proxyPath + "/.*", new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest request) {
                request.response().setStatusCode(HttpResponseStatus.METHOD_NOT_ALLOWED.code());
                request.response().end();
            }
        });

        //No match pattern
        routeMatcher.noMatch(new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest request) {
                request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
                request.response().end();
            }
        });

        final HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(routeMatcher);
        httpServer.listen(port);

        container.logger().info("Webserver proxy started, listening on port:" + port);

    }

    private List<RepositoryObject> buildRepoUrls(JsonArray repositories) {
        List<RepositoryObject> result = new ArrayList<RepositoryObject>();
        for (Object repository : repositories) {
            JsonObject repositoryOject = (JsonObject) repository;

            final Map<String, Object> stringObjectMap = repositoryOject.toMap();
            for (Map.Entry<String, Object> stringObjectEntry : stringObjectMap.entrySet()) {
                final Map<String, Object> repoMap = (Map<String, Object>) stringObjectEntry.getValue();
                result.add(new RepositoryObject((String) repoMap.get("host"),
                        (int) repoMap.get("port"), (String) repoMap.get("uri")));
            }
        }
        return result;
    }
}
