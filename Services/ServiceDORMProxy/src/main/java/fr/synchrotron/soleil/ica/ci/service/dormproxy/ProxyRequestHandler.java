package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerFileUpload;
import org.vertx.java.core.http.HttpServerRequest;

import java.io.File;


/**
 * @author Gregory Boissinot
 */
public class ProxyRequestHandler implements Handler<HttpServerRequest> {

    private static final File REPO_ROOT_DIRECTORY = new File("/tmp/repo");

    private final Vertx vertx;

    public ProxyRequestHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final String prefix = DORMProxyEndpointVerticle.PROXY_PATH;
        String artifactPath = request.path().substring(prefix.length() + 1);
        //TODO check
        final File uploadedDirectory = new File(REPO_ROOT_DIRECTORY, artifactPath.substring(0, artifactPath.lastIndexOf("/")));
        final String filename = artifactPath.substring(artifactPath.lastIndexOf("/")+1);
        final File uploadedFile = new File(uploadedDirectory, filename);

        request.expectMultiPart(true);

        request.uploadHandler(new Handler<HttpServerFileUpload>() {
            @Override
            public void handle(final HttpServerFileUpload upload) {


                upload.exceptionHandler(new Handler<Throwable>() {
                                            @Override
                                            public void handle(Throwable throwable) {
                                                request.response().setStatusCode(500);
                                                request.response().setStatusMessage(throwable.getMessage());
                                                request.response().end();
                                            }
                                        }
                );

                upload.endHandler(new Handler<Void>() {
                    @Override
                    public void handle(Void event) {
                        request.response().setStatusCode(204);
                        request.response().end();
                    }
                });

                vertx.fileSystem().mkdir(uploadedDirectory.getPath(), true, new AsyncResultHandler<Void>() {
                    @Override
                    public void handle(AsyncResult<Void> asyncResult) {
                        if (!asyncResult.succeeded()) {
                            throw new RuntimeException(asyncResult.cause());
                        }
                    }
                });


                upload.streamToFileSystem(uploadedFile.getAbsolutePath());
            }
        });

    }


}