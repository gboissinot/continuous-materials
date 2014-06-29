package fr.synchrotron.soleil.ica.ci.service.dornmanagement;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */
public class DeployerVerticle extends Verticle {

    @Override
    public void start() {

        final JsonObject serviceConfig = container.config();
        container.deployVerticle(HttpDORMManagementEndpointVerticle.class.getCanonicalName(), serviceConfig, 2);

        final JsonObject mongoConfig = serviceConfig.getObject("mongo");
        container.deployWorkerVerticle(ProjectPersistorWorkerVerticle.class.getCanonicalName(), mongoConfig, 3);

        final JsonObject jenkinsConfig = serviceConfig.getObject("jenkins");
        container.deployVerticle(JenkinsJobVerticle.class.getCanonicalName(), jenkinsConfig, 3);

    }

}
