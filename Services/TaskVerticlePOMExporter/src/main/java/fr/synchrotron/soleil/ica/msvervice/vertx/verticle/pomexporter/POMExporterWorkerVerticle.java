package fr.synchrotron.soleil.ica.msvervice.vertx.verticle.pomexporter;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.repository.POMDocumentRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.service.POMExportService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.io.StringWriter;

/**
 * @author Gregory Boissinot
 */
public class POMExporterWorkerVerticle extends Verticle {

    @Override
    public void start() {

        final POMExportService pomExportService =
                new POMExportService(new POMDocumentRepository(getBasicMongoDBDataSource(container.config())));
        final EventBus eventBus = vertx.eventBus();
        eventBus.registerHandler("pom.exporter", new Handler<Message>() {
            @Override
            public void handle(Message message) {
                try {
                    JsonObject pomIdObject = (JsonObject) message.body();
                    String org = pomIdObject.getString("org");
                    String name = pomIdObject.getString("name");
                    String version = pomIdObject.getString("version");
                    String status = pomIdObject.getString("status");
                    StringWriter stringWriter = new StringWriter();
                    pomExportService.exportPomFile(stringWriter, org, name, version, status);
                    message.reply(stringWriter.toString());
                } catch (Throwable e) {
                    message.reply(e.getMessage());
                }
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
