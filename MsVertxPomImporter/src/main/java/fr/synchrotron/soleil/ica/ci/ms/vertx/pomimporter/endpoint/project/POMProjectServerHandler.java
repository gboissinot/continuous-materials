package fr.synchrotron.soleil.ica.ci.ms.vertx.pomimporter.endpoint.project;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.repository.POMRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.POMManagementService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import org.vertx.java.core.*;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.streams.Pump;

import java.io.File;
import java.util.UUID;

/**
 * @author Gregory Boissinot
 */
public class POMProjectServerHandler implements Handler<HttpServerRequest> {

    private final Vertx vertx;

    public POMProjectServerHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(final HttpServerRequest req) {

        req.pause();

        final File uploadedDirectory = new File("upload");
        uploadedDirectory.mkdirs();
        final String filename = "file-" + UUID.randomUUID().toString() + ".upload";
        final File uploadedFile = new File(uploadedDirectory, filename);

        vertx.fileSystem().open(uploadedFile.getPath(), new AsyncResultHandler<AsyncFile>() {
            public void handle(AsyncResult<AsyncFile> ar) {
                if (ar.failed()) {
                    ar.cause().printStackTrace();
                    return;
                }
                final AsyncFile file = ar.result();
                final Pump pump = Pump.createPump(req, file);
                final long start = System.currentTimeMillis();
                req.endHandler(new VoidHandler() {
                    public void handle() {
                        file.close(new AsyncResultHandler<Void>() {
                            public void handle(AsyncResult<Void> ar) {
                                if (ar.succeeded()) {
                                    final HttpServerResponse response = req.response();
                                    try {
                                        final BasicMongoDBDataSource mongoDBDataSource = new BasicMongoDBDataSource("127.0.0.1", 27017, "repo");
                                        final POMRepository pomRepository = new POMRepository(mongoDBDataSource);
                                        POMManagementService pomManagementService = new POMManagementService(pomRepository);

                                        response.setStatusCode(200);
                                        pomManagementService.insertProjectDocument(uploadedFile);
                                        response.setStatusMessage("OK.");
                                        String okMessage = "POM File inserted in MongoDB\n";
                                        response.putHeader("Content-Length", String.valueOf(okMessage.getBytes().length));
                                        response.write(okMessage);
                                        response.end();
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                        response.setStatusCode(500);
                                        response.end();
                                    }

                                    long end = System.currentTimeMillis();
                                    System.out.println("Uploaded " + pump.bytesPumped() + " bytes to " + filename + " in " + (end - start) + " ms");
                                } else {
                                    ar.cause().printStackTrace(System.err);
                                }
                            }
                        });
                    }
                });
                pump.start();
                req.resume();


            }
        });

        uploadedFile.deleteOnExit();

    }
}
