package fr.synchrotron.soleil.ica.msvervice.vertx.verticle.pomimport;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.POMImportService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */
public class POMImporterWorkerVerticle extends Verticle {

    @Override
    public void start() {

        final JsonObject config = container.config();

        final EventBus eventBus = vertx.eventBus();
        eventBus.registerHandler("pom.importer", new Handler<Message>() {
            @Override
            public void handle(Message message) {
                final BasicMongoDBDataSource mongoDBDataSource = getBasicMongoDBDataSource(config);
                POMImportService pomImportService = new POMImportService(mongoDBDataSource);
                pomImportService.importPomFile(String.valueOf(message.body()));
                message.reply("POM file inserted in Metadata Registry.");
            }
        });
    }

    private BasicMongoDBDataSource getBasicMongoDBDataSource(JsonObject config) {
        return new BasicMongoDBDataSource(
                config.getString("mongo.host"),
                Integer.parseInt(config.getString("mongo.port")),
                config.getString("mongo.dbname"));
    }

}


