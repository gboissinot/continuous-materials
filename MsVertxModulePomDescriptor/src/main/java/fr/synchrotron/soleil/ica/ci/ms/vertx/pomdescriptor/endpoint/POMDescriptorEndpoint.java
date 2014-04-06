package fr.synchrotron.soleil.ica.ci.ms.vertx.pomdescriptor.endpoint;

import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Gregory Boissinot
 */
public class POMDescriptorEndpoint extends Verticle {

    public static final int PORT = 8080;

    private final String mongoHost;
    private final int mongoPort;
    private final String mongoDBName;

    public POMDescriptorEndpoint() {
        Properties properties = new Properties();
        try {
            properties.load(POMDescriptorEndpoint.class.getResourceAsStream("/infra.properties"));
            mongoHost = properties.getProperty("mongo.host");
            mongoDBName = properties.getProperty("mongo.dbname");
            try {
                String mongoPortStr = properties.getProperty("mongo.port");
                mongoPort = Integer.parseInt(mongoPortStr);
            } catch (NumberFormatException nfe) {
                throw new RuntimeException(nfe);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public void start() {

        try {
            RouteMatcher routeMatcher = new RouteMatcher();
            routeMatcher.allWithRegEx("/pom/.*", new POMDescriptorServerHandler(vertx, mongoHost, mongoPort, mongoDBName));

            HttpServer httpServer = vertx.createHttpServer();
            httpServer.requestHandler(routeMatcher);
            httpServer.listen(PORT);

            container.logger().info("Vert.x instance started, listening on port:" + PORT);

        } catch (Throwable e) {
            System.out.println(e);
        }
    }


}
