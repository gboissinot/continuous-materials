package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pull;

import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.RepositoryRequestBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.streams.Pump;

/**
 * @author Gregory Boissinot
 */
public class GETHandler implements Handler<HttpServerRequest> {

    protected Vertx vertx;
    protected HttpClient vertxHttpClient;
    protected RepositoryRequestBuilder repositoryRequestBuilder;

    public GETHandler(Vertx vertx,
                      HttpClient vertxHttpClient,
                      String proxyPath,
                      String repoHost,
                      int repoPort,
                      String repoUri) {
        this.vertx = vertx;
        this.vertxHttpClient = vertxHttpClient;
        this.repositoryRequestBuilder = new RepositoryRequestBuilder(proxyPath, repoHost, repoPort, repoUri);
    }


    @Override
    public void handle(final HttpServerRequest request) {

        final String path = repositoryRequestBuilder.buildRequestPath(request);
        System.out.println("Download " + path);

        HttpClientRequest vertxHttpClientRequest = vertxHttpClient.get(path, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {

                clientResponse.pause();
                final int statusCode = clientResponse.statusCode();
                request.response().setStatusCode(statusCode);
                request.response().setStatusMessage(clientResponse.statusMessage());
                request.response().headers().set(clientResponse.headers());
                request.response().setChunked(true);
                clientResponse.endHandler(new Handler<Void>() {
                    public void handle(Void event) {
                        request.response().end();
                    }
                });
                if (statusCode == HttpResponseStatus.NOT_MODIFIED.code()
                        || statusCode == HttpResponseStatus.OK.code()) {
                    final Pump pump = Pump.createPump(clientResponse, request.response());
                    pump.start();
                }
                clientResponse.resume();

            }
        });

        vertxHttpClientRequest.headers().set(request.headers());
        vertxHttpClientRequest.headers().remove("Host");
        vertxHttpClientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("Exception from ").append(repositoryRequestBuilder.getRepoHost());
                errorMsg.append("-->").append(throwable.toString());
                errorMsg.append("\n");
                request.response().end(errorMsg.toString());
            }
        });
        vertxHttpClientRequest.end();

    }
}
