package fr.synchrotron.soleil.ica.ci.ms.vertx.pomimporter.endpoint;

import fr.synchrotron.soleil.ica.ci.ms.vertx.pomimporter.endpoint.project.POMProjectServerHandler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */
public class PomMongoEndpoint extends Verticle {

    public static final int PORT = 8080;

    public void start() {

        try {
            RouteMatcher routeMatcher = new RouteMatcher();
            routeMatcher.allWithRegEx("/pomimportert/importProject/.*", new POMProjectServerHandler(vertx));

            HttpServer httpServer = vertx.createHttpServer();
            httpServer.requestHandler(routeMatcher);
            httpServer.listen(PORT);

            container.logger().info("Vert.x instance started, listening on port:" + PORT);

        } catch (Throwable e) {
            System.out.println(e);
        }
    }


}
