package fr.synchrotron.soleil.ica.ci.service.dornmanagement;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Gregory Boissinot
 */
public class JenkinsJobHandler implements Handler<HttpServerRequest> {

    private Vertx vertx;

    public JenkinsJobHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        JsonObject message = new JsonObject();
        message.putString("action", "load");
        vertx.eventBus().sendWithTimeout("dorm.management.project.persistor", message, Integer.MAX_VALUE, new AsyncResultHandler<Message<JsonArray>>() {
            @Override
            public void handle(AsyncResult<Message<JsonArray>> asyncResult) {
                if (asyncResult.failed()) {
                    sendError(request, asyncResult.cause());
                } else {
                    processJenkinsProjects(request, asyncResult.result().body());
                }
            }
        });
    }

    private void sendError(HttpServerRequest request, Throwable throwable) {
        request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        if (throwable != null) {
            throwable.printStackTrace();
            request.response().setStatusMessage(throwable.getMessage());
        }
        request.response().end();
    }

    private void sendOK(HttpServerRequest request) {
        request.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code());
        request.response().end();
    }

    private void processJenkinsProjects(final HttpServerRequest request, JsonArray projects) {

        for (Object projectObject : projects) {
            JsonObject project = (JsonObject) projectObject;
            processProject(request, project.getString("name"));
        }

        //Send immediately the OK response
        sendOK(request);

    }

    private void processProject(final HttpServerRequest request, final String jobName) {
        JsonObject isExistJobMessage = new JsonObject();
        isExistJobMessage.putString("action", "testJobExist");
        JsonObject content = new JsonObject();
        content.putString("jobName", jobName);
        isExistJobMessage.putObject("content", content);

        vertx.eventBus().sendWithTimeout("dorm.management.jenkins.job", isExistJobMessage, Integer.MAX_VALUE, new AsyncResultHandler<Message<Boolean>>() {
            @Override
            public void handle(AsyncResult<Message<Boolean>> asyncResult) {
                if (asyncResult.failed()) {
                    throw new RuntimeException(asyncResult.cause());
                } else {
                    final Boolean isExistJob = asyncResult.result().body();
                    System.out.println(isExistJob);
                    createOrUpdateProject(request, jobName, isExistJob);
                }
            }
        });
    }

    private void createOrUpdateProject(final HttpServerRequest request,
                                       final String jobName,
                                       final boolean isExistJob) {

        JsonObject createJobMessage = new JsonObject();
        if (isExistJob) {
            createJobMessage.putString("action", "updateJob");
        } else {
            createJobMessage.putString("action", "createJob");
        }
        JsonObject content = new JsonObject();
        content.putString("jobName", jobName);
        createJobMessage.putObject("content", content);

        vertx.eventBus().sendWithTimeout("dorm.management.jenkins.job", createJobMessage, Integer.MAX_VALUE, new AsyncResultHandler<Message<Boolean>>() {
            @Override
            public void handle(AsyncResult<Message<Boolean>> asyncResult) {
                if (asyncResult.failed()) {
                    asyncResult.cause().printStackTrace();
                } else {
                    System.out.println("Creation or update OK");
                }
            }
        });
    }

}
