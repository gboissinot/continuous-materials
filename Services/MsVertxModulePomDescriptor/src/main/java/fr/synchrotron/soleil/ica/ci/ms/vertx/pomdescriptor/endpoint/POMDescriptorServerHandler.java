package fr.synchrotron.soleil.ica.ci.ms.vertx.pomdescriptor.endpoint;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.repository.POMDocumentRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomexporter.service.POMExportService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.POMImportService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.streams.Pump;

import java.io.File;
import java.io.StringWriter;
import java.util.UUID;

/**
 * @author Gregory Boissinot
 */
public class POMDescriptorServerHandler implements Handler<HttpServerRequest> {

    private final Vertx vertx;
    private final String mongoHost;
    private final int mongoPort;
    private final String mongoDBName;

    public POMDescriptorServerHandler(Vertx vertx, String mongoHost, int mongoPort, String mongoDBName) {
        this.vertx = vertx;
        this.mongoHost = mongoHost;
        this.mongoPort = mongoPort;
        this.mongoDBName = mongoDBName;
    }

    @Override
    public void handle(final HttpServerRequest request) {
        try {
            final String method = request.method();

            if ("PUT".equals(method) || "POST".equals(method)) {
                handleUploadPom(request);
            } else if ("GET".equals(method)) {
                handleDownloadPom(request);
            } else {
                request.response().setStatusCode(400);
                request.response().setStatusMessage("Only GET, PUT and POST requests are supported.");
                request.response().end();
            }
        } catch (Throwable e) {
            request.response().setStatusCode(500);
            request.response().setStatusMessage(e.toString());
            request.response().end();
        }

    }

    private void handleDownloadPom(final HttpServerRequest request) {
        final BasicMongoDBDataSource mongoDBDataSource = new BasicMongoDBDataSource(mongoHost, mongoPort, mongoDBName);
        final POMDocumentRepository pomDocumentRepository = new POMDocumentRepository(mongoDBDataSource);
        POMExportService pomExportService = new POMExportService(pomDocumentRepository);

        final MultiMap params = request.params();
        String org = params.get("org");
        String name = params.get("name");
        String status = params.get("status");
        String version = params.get("version");

        StringWriter stringWriter = new StringWriter();
        pomExportService.exportPomFile(stringWriter, org, name, status, version);
        String pomContent = stringWriter.toString();

        request.response().setStatusCode(200);
        request.response().putHeader("Content-Length", String.valueOf(pomContent.getBytes().length));
        request.response().write(pomContent);
        request.response().end();
    }

    private void handleUploadPom(final HttpServerRequest req) {

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
                                        final BasicMongoDBDataSource mongoDBDataSource = new BasicMongoDBDataSource(mongoHost, mongoPort, mongoDBName);
                                        POMImportService pomImportService = new POMImportService(mongoDBDataSource);

                                        response.setStatusCode(200);
                                        pomImportService.importPomFile(uploadedFile);
                                        response.setStatusMessage("OK.");
                                        String okMessage = "POM file inserted in MongoDB\n";
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
