package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */
public class DeployerVerticle extends Verticle {

    @Override
    public void start() {
        final JsonObject config = container.config();
        container.deployVerticle(DORMProxyEndpointVerticle.class.getCanonicalName(), config);

        final JsonObject mongo = config.getObject("mongo");
        container.deployWorkerVerticle(POMMetadataWorkerVerticle.class.getCanonicalName(), mongo, 3);
    }
}
