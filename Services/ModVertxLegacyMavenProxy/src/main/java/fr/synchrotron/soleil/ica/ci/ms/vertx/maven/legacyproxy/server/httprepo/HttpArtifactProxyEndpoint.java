package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.legacyproxy.server.httprepo;

import fr.synchrotron.soleil.ica.ci.ms.vertx.maven.legacyproxy.server.javaconfig.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.Map;
import java.util.Properties;

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

            final JsonObject config = container.config();
            final Map<String, Object> configMap = config.toMap();
            Properties properties = new Properties();
            for (String key : configMap.keySet()) {
                properties.put(key, configMap.get(key));
            }
            PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
            configurer.setProperties(properties);
            configurer.setIgnoreUnresolvablePlaceholders(true);
            applicationContext.addBeanFactoryPostProcessor(configurer);
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
            container.logger().error(e);
            if (httpServer != null) {
                httpServer.close();
            }
        }
    }

}

