package fr.synchrotron.soleil.ica.msvervice.vertx.verticle.pomimport;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.POMImportService;
import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.MongoDBUtilities;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */
public class POMImporterWorkerVerticle extends Verticle {

    private MongoDBUtilities mongoDBUtilities;

    public POMImporterWorkerVerticle() {
        this.mongoDBUtilities = new MongoDBUtilities();
    }

    @Override
    public void start() {

        final POMImportService pomImportService =
                new POMImportService(mongoDBUtilities.getBasicMongoDBDataSource(container.config()));
        final EventBus eventBus = vertx.eventBus();
        eventBus.registerHandler("pom.importer", new Handler<Message>() {
            @Override
            public void handle(Message message) {
                try {
                    pomImportService.importPomFile(String.valueOf(message.body()));
                    message.reply("POM file inserted in Metadata Registry.");
                } catch (Throwable e) {
                    message.reply(e.getMessage());
                }
            }
        });
    }

}


