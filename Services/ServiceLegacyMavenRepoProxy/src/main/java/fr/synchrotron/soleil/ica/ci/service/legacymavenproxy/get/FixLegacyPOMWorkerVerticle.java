package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.get;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.mongodb.MongoDBArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.ServiceAddressRegistry;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

import java.io.StringWriter;

/**
 * @author Gregory Boissinot
 */
public class FixLegacyPOMWorkerVerticle extends BusModBase {

    @Override
    public void start() {

        super.start();
        final String mongoHost = getMandatoryStringConfig("mongoHost");
        final Integer mongoPort = getMandatoryIntConfig("mongoPort");
        final String mongoDbName = getMandatoryStringConfig("mongoDbName");
        final LegacyPomContentFixer legacyPomContentFixer =
                new LegacyPomContentFixer(new MongoDBArtifactRepository(
                        new BasicMongoDBDataSource(mongoHost, mongoPort, mongoDbName)));

        eb.registerHandler(ServiceAddressRegistry.EB_ADDRESS_FIXLEGACYPOM_SERVICE, new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> message) {
                try {
                    final Model resolvedPomModel = legacyPomContentFixer.getModelWithResolvedParent(message.body());
                    StringWriter stringWriter = new StringWriter();
                    new MavenXpp3Writer().write(stringWriter, resolvedPomModel);
                    message.reply(stringWriter.toString());
                } catch (Throwable e) {
                    //TODO BUILD ERROR MESSAGE
                    message.reply(e.getMessage());
                }

            }
        });
    }
}
