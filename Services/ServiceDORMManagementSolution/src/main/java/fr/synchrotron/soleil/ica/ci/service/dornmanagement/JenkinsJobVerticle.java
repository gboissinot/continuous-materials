package fr.synchrotron.soleil.ica.ci.service.dornmanagement;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Gregory Boissinot
 */
public class JenkinsJobVerticle extends BusModBase {

    private static final String ACTION_TEST_JOB_EXIST = "testJobExist";
    private static final String ACTION_CREATE_JOB = "createJob";
    private static final String ACTION_UPDATE_JOB = "updateJob";

    @Override
    public void start() {

        super.start();
        final JsonObject jenkinsURLObject = config.getObject("url");
        final String templateDirPath = config.getString("config.template.dir");

        eb.registerHandler("dorm.management.jenkins.job",
                new Handler<Message<JsonObject>>() {
                    @Override
                    public void handle(Message<JsonObject> message) {
                        try {
                            String action = message.body().getString("action");

                            switch (action) {
                                case ACTION_TEST_JOB_EXIST:
                                    processIsJobExist(jenkinsURLObject, message);
                                    break;
                                case ACTION_CREATE_JOB:
                                    processCreateOrUpdateJob(jenkinsURLObject, templateDirPath, true, message);
                                    break;
                                case ACTION_UPDATE_JOB:
                                    processCreateOrUpdateJob(jenkinsURLObject, templateDirPath, false, message);
                                    break;
                                default:
                                    message.fail(-1, "Wrong Verticle Action in ProjectPersistorWorkerVerticle.");
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            message.fail(-1, e.getMessage());
                        }
                    }
                }
        );
    }

    private void processIsJobExist(JsonObject jenkinsURLObject, final Message<JsonObject> message) {

        JsonObject messageContent = message.body().getObject("content");
        String jobName = messageContent.getString("jobName");

        HttpClient httpClient = getHttpClient(jenkinsURLObject);
        String path = jenkinsURLObject.getString("path") + "/job/" + jobName;
        HttpClientRequest clientRequest = httpClient.head(path, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                int statusCode = clientResponse.statusCode();
                message.reply(statusCode != HttpResponseStatus.NOT_FOUND.code());
            }
        });
        clientRequest.end();
    }

    private void processCreateOrUpdateJob(final JsonObject jenkinsURLObject,
                                          String templateDirPath,
                                          final boolean createJob,
                                          final Message<JsonObject> message) {
        final String filePath = templateDirPath + "template_jenkinsJob.xml";
        vertx.fileSystem().exists(filePath, new AsyncResultHandler<Boolean>() {
            public void handle(AsyncResult<Boolean> ar) {
                if (ar.succeeded()) {
                    vertx.fileSystem().readFile(filePath, new AsyncResultHandler<Buffer>() {
                        @Override
                        public void handle(AsyncResult<Buffer> asyncFileAsyncResult) {
                            if (asyncFileAsyncResult.failed()) {
                                asyncFileAsyncResult.cause().printStackTrace();
                            }

                            if (asyncFileAsyncResult.succeeded()) {
                                createOrUpdateJob(jenkinsURLObject, createJob,
                                        asyncFileAsyncResult.result().toString(),
                                        message);
                            }
                        }
                    });
                } else {
                    ar.cause().printStackTrace();
                }
            }
        });
    }

    private void createOrUpdateJob(JsonObject jenkinsURLObject,
                                   boolean createJob, String configXML,
                                   final Message<JsonObject> message) {

        JsonObject messageContent = message.body().getObject("content");
        String jobName = messageContent.getString("jobName");

        HttpClient httpClient = getHttpClient(jenkinsURLObject);
        String path;
        if (createJob) {
            //curl -X POST -H "Content-Type:application/xml" -d @config.xml "http://JENKINS_HOST/createItem?name=JOB_NAME"
            path = jenkinsURLObject.getString("path") + "/createItem?name=" + jobName;
        } else {
            //curl -X POST -H "Content-Type:application/xml" -d @config.xml "http://localhost:8080/job/sample-job/config.xml"
            path = jenkinsURLObject.getString("path") + "/job/" + jobName + "/config.xml";
        }
        HttpClientRequest clientRequest = httpClient.post(path, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {
                int statusCode = clientResponse.statusCode();
                message.reply(statusCode == HttpResponseStatus.OK.code());
            }
        });
        clientRequest.headers().set(HttpHeaders.CONTENT_TYPE, "application/xml");
        clientRequest.headers().set(HttpHeaders.CONTENT_LENGTH, String.valueOf(configXML.getBytes().length));
        clientRequest.write(configXML);
        clientRequest.end();
    }

    private HttpClient getHttpClient(JsonObject jenkinsURLObject) {
        return vertx.createHttpClient()
                .setHost(jenkinsURLObject.getString("host"))
                .setPort(jenkinsURLObject.getInteger("port"));
    }


}
