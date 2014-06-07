package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.legacyproxy.server.httprepo.push;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.POMImportService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.dictionary.SoleilDictionary;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

/**
 * @author Gregory Boissinot
 */
public class TrackPOMWorkerVerticle extends BusModBase {

    private static final String EB_ADDRESS = "pom.track";

    @Override
    public void start() {

        super.start();
        final String mongoHost = getMandatoryStringConfig("mongoHost");
        final Integer mongoPort = getMandatoryIntConfig("mongoPort");
        final String mongoDbName = getMandatoryStringConfig("mongoDbName");
        final POMImportService pomImportService =
                new POMImportService(new SoleilDictionary(), new BasicMongoDBDataSource(mongoHost, mongoPort, mongoDbName));

        eb.registerHandler(EB_ADDRESS, new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> message) {
                try {
                    pomImportService.importPomFile(message.body());
                    message.reply(true);
                } catch (Throwable e) {
                    //TODO BUILD ERROR MESSAGE
                    message.reply(e.getMessage());
                }
            }
        });
    }
}
