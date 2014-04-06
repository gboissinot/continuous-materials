package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.server.httprepo;

import fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.server.javaconfig.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */

public class HttpArtifactProxyEndpoint extends Verticle {

    public static final int port = 8090;

    public void start() {
        HttpServer httpServer = null;
        try {
            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
            applicationContext.getEnvironment().setActiveProfiles("repoHttp");
            applicationContext.register(AppConfig.class);
            applicationContext.refresh();

            httpServer = vertx.createHttpServer();

            HttpArtifactServerHandler httpArtifactServerHandler = applicationContext.getBean(HttpArtifactServerHandler.class);
            httpArtifactServerHandler.setVertx(vertx);

            RouteMatcher routeMatcher = new RouteMatcher();
            routeMatcher.allWithRegEx("/maven/.*", httpArtifactServerHandler);
            httpServer.requestHandler(routeMatcher);
            httpServer.listen(port);

            container.logger().info("Webserver proxy started, listening on port:" + port);

        } catch (Throwable e) {
            System.out.println(e);
            if (httpServer != null) {
                httpServer.close();
            }
        }
    }

}

