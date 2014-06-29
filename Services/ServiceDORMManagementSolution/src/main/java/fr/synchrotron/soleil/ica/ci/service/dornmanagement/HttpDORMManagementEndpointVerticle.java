package fr.synchrotron.soleil.ica.ci.service.dornmanagement;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;

/**
 * @author Gregory Boissinot
 */
public class HttpDORMManagementEndpointVerticle extends BusModBase {

    @Override
    public void start() {
        super.start();

        final int port = getMandatoryIntConfig("service.port");
        final String path = getMandatoryStringConfig("service.path");

        HttpServer httpServer = null;
        try {
            RouteMatcher routeMatcher = new RouteMatcher();
            routeMatcher.put(path + "/syncJenkinsJobs", new JenkinsJobHandler(vertx));

            httpServer = vertx.createHttpServer();
            httpServer.requestHandler(routeMatcher);
            httpServer.listen(port);

            container.logger().info("DORMManagement service started, listening on port:" + port);

        } catch (Throwable e) {
            container.logger().error(e);
            if (httpServer != null) {
                httpServer.close();
            }
        }
    }
}
