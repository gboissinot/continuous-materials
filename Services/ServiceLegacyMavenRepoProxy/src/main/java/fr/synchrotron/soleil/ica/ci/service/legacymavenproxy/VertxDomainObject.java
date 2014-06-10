package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.logging.Logger;

/**
 * @author Gregory Boissinot
 */
public class VertxDomainObject {

    private final Vertx vertx;

    private final Logger logger;

    public VertxDomainObject(Vertx vertx, Logger logger) {
        this.vertx = vertx;
        this.logger = logger;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public Logger getLogger() {
        return logger;
    }
}
