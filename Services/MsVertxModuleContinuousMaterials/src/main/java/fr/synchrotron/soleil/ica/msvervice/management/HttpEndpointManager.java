package fr.synchrotron.soleil.ica.msvervice.management;

import fr.synchrotron.soleil.ica.msvervice.management.handlers.POMImportHandler;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @author Gregory Boissinot
 */
public class HttpEndpointManager extends Verticle {


    public static final long SEND_MS_TIMEOUT = 10 * 1000l; // in ms

    @Override
    public void start() {

        final EventBus eventBus = vertx.eventBus();

        container.deployWorkerVerticle(
                "fr.synchrotron.soleil.ica.msvervice.vertx.verticle.pomimport.POMImporterWorkerVerticle",
                createConfig(),
                1,
                true,
                new AsyncResultHandler<String>() {
                    @Override
                    public void handle(AsyncResult<String> asyncResult) {
                        onVerticleLoaded(asyncResult);
                    }
                }
        );

        RouteMatcher routeMatcher = new RouteMatcher();

        //-- POM IMPORTER
        final POMImportHandler pomImportHandler = new POMImportHandler(eventBus);
        routeMatcher.post("/import", pomImportHandler);
        routeMatcher.put("/import", pomImportHandler);

        routeMatcher.allWithRegEx(".*", new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest request) {
                request.response().end("Path or Http method not supported.\n");
            }
        });
        vertx.createHttpServer().requestHandler(routeMatcher).listen(8080);
        container.logger().info("Webserver  started");
    }


    private void onVerticleLoaded(AsyncResult<String> asyncResult) {
        if (!asyncResult.succeeded()) {
            container.logger().info(asyncResult.cause());
        }
    }

    private JsonObject createConfig() {
        final JsonObject config = container.config();
        Properties properties = loadInfraFile();
        for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
            String propKey = (String) objectObjectEntry.getKey();
            if (!config.containsField(propKey)) {
                String propValue = (String) objectObjectEntry.getValue();
                config.putString(propKey, propValue);
            }
        }
        return config;
    }

    private Properties loadInfraFile() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/infra.properties"));
            return properties;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
