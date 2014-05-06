package fr.synchrotron.soleil.ica.msvervice.vertx.mavenmetadata;

import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.VertxConfigLoader;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */
public class StarterVerticle extends Verticle {

    @Override
    public void start() {

        final VertxConfigLoader vertxConfigLoader = new VertxConfigLoader();
        final JsonObject config = vertxConfigLoader.createConfig(container.config());

        final AsyncResultHandler<String> asyncResultHandler = new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
                onVerticleLoaded(asyncResult);
            }
        };

        container.deployWorkerVerticle(
                POMImporterWorkerVerticle.class.getCanonicalName(),
                config, 1, true, asyncResultHandler);

        container.deployWorkerVerticle(
                POMExporterWorkerVerticle.class.getCanonicalName(),
                config, 1, true, asyncResultHandler);
    }

    private void onVerticleLoaded(AsyncResult<String> asyncResult) {
        if (!asyncResult.succeeded()) {
            container.logger().info(asyncResult.cause());
        }
    }
}
