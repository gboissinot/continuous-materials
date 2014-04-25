package fr.synchrotron.soleil.ica.msvervice.vertx.verticle.pomexporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.repository.POMDocumentRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.service.POMExportService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ObjectMapperUtilities;
import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.MongoDBUtilities;
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

    private MongoDBUtilities mongoDBUtilities;

    public POMExporterWorkerVerticle() {
        this.mongoDBUtilities = new MongoDBUtilities();
    }

    @Override
    public void start() {

        final POMExportService pomExportService =
                new POMExportService(new POMDocumentRepository(mongoDBUtilities.getBasicMongoDBDataSource(container.config())));
        final EventBus eventBus = vertx.eventBus();
        eventBus.registerHandler("pom.exporter", new Handler<Message>() {
            @Override
            public void handle(Message message) {
                try {
                    JsonObject pomIdObject = (JsonObject) message.body();
                    ObjectMapperUtilities objectMapperUtilities = new ObjectMapperUtilities();
                    final ObjectMapper objectMapper = objectMapperUtilities.getObjectMapper();
                    final ArtifactDocumentKey artifactDocumentKey = objectMapper.convertValue(pomIdObject.toMap(), ArtifactDocumentKey.class);
                    StringWriter stringWriter = new StringWriter();
                    pomExportService.exportPomFile(stringWriter, artifactDocumentKey);
                    message.reply(stringWriter.toString());
                } catch (Throwable e) {
                    message.fail(500, e.getMessage());
                }
            }
        });
    }


}
