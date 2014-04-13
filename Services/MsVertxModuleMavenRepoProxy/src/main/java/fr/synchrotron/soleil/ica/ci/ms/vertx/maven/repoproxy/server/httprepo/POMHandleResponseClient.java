package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.server.httprepo;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.mongodb.MongoDBArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.service.PomArtifactWriter;
import fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.service.PomModelBuilder;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * @author Gregory Boissinot
 */
public class POMHandleResponseClient extends HandleResponseClient {

    private final String mongoHost;
    private final int mongoPort;
    private final String mongoDbName;

    public POMHandleResponseClient(HttpServerRequest request, String mongoHost, int mongoPort, String mongoDbName) {
        super(request);
        this.mongoHost = mongoHost;
        this.mongoPort = mongoPort;
        this.mongoDbName = mongoDbName;
    }

    @Override
    public void handle(HttpClientResponse clientResponse) {

        int statusCode = clientResponse.statusCode();
        if (statusCode == 404) {
            request.response().setStatusCode(statusCode);
            request.response().end();
            return;
        }

        clientResponse.bodyHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer data) {
                try {
                    final PomModelBuilder pomModelBuilder =
                            new PomModelBuilder(new MongoDBArtifactRepository(new BasicMongoDBDataSource(mongoHost, mongoPort, mongoDbName)));

                    new PomArtifactWriter()
                            .writePomArtifact(
                                    request,
                                    pomModelBuilder.getModelWithResolvedParent(data.toString()));
                } catch (Throwable e) {
                    request.response().setStatusCode(500);
                    request.response().end(e.toString());
                }
            }
        });

    }

}
