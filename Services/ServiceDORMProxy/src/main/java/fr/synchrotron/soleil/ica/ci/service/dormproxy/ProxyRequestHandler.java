package fr.synchrotron.soleil.ica.ci.service.dormproxy;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerFileUpload;
import org.vertx.java.core.http.HttpServerRequest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Gregory Boissinot
 */
public class ProxyRequestHandler implements Handler<HttpServerRequest> {

    private final Vertx vertx;
    private final String fsRepositoryRootDir;

    private Map<Integer, StringBuilder> pomStorage = new HashMap<Integer, StringBuilder>();

    public ProxyRequestHandler(Vertx vertx, String fsRepositoryRootDir) {
        this.vertx = vertx;
        this.fsRepositoryRootDir = fsRepositoryRootDir;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final String path = request.path();
        if (path.endsWith(".jar")) {
            uploadJarFile(request, path);
        } else if (path.endsWith(".pom")) {
            uploadPomFile(request, path);
        } else {
            request.response().setStatusCode(HttpResponseStatus.NOT_MODIFIED.code());
            request.response().end();
        }
    }

    private void uploadPomFile(final HttpServerRequest request, final String path) {
        request.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer data) {

                int requestId = request.path().hashCode();
                StringBuilder content = pomStorage.get(requestId);
                if (content == null) {
                    pomStorage.put(requestId, new StringBuilder(data.toString()));
                } else {
                    pomStorage.put(requestId, content.append(data.toString()));
                }
            }
        });

        request.endHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                int code = request.path().hashCode();
                StringBuilder content = pomStorage.get(code);
                vertx.eventBus().sendWithTimeout(
                        ServiceAddressRegistry.EB_ADDRESS_POMIMPORT_SERVICE,
                        content.toString(), Integer.MAX_VALUE, new AsyncResultHandler<Message<Boolean>>() {
                            @Override
                            public void handle(AsyncResult<Message<Boolean>> asyncResult) {
                                if (asyncResult.succeeded()) {
                                    request.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code());
                                    request.response().end();
                                } else {
                                    request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                                    request.response().setStatusMessage(asyncResult.cause().getMessage());
                                    request.response().end();
                                }
                            }
                        }
                );
            }
        });
    }

    private void uploadJarFile(final HttpServerRequest request, String path) {
        final String prefix = DORMProxyEndpointVerticle.PROXY_PATH;
        String artifactPath = path.substring(prefix.length() + 1);
        //TODO check
        final File uploadedDirectory = new File(fsRepositoryRootDir, artifactPath.substring(0, artifactPath.lastIndexOf("/")));
        final String filename = artifactPath.substring(artifactPath.lastIndexOf("/") + 1);
        final File uploadedFile = new File(uploadedDirectory, filename);

        request.expectMultiPart(true);

        request.uploadHandler(new Handler<HttpServerFileUpload>() {
            @Override
            public void handle(final HttpServerFileUpload upload) {

                upload.exceptionHandler(new Handler<Throwable>() {
                    @Override
                    public void handle(Throwable throwable) {
                        request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                        request.response().setStatusMessage(throwable.getMessage());
                        request.response().end();
                    }
                });

                upload.endHandler(new Handler<Void>() {
                    @Override
                    public void handle(Void event) {
                        request.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code());
                        request.response().end();
                    }
                });

                vertx.fileSystem().mkdir(uploadedDirectory.getPath(), true, new AsyncResultHandler<Void>() {
                    @Override
                    public void handle(AsyncResult<Void> asyncResult) {
                        if (asyncResult.failed()) {
                            throw new RuntimeException(asyncResult.cause());
                        }
                    }
                });

                upload.streamToFileSystem(uploadedFile.getAbsolutePath());
            }
        });

    }


}