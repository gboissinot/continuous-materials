package fr.synchrotron.soleil.ica.ci.service.dornmanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.ProjectRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class ProjectPersistorWorkerVerticle extends BusModBase {

    private static final String ACTION_LOAD = "load";

    @Override
    public void start() {

        super.start();
        final String mongoHost = getMandatoryStringConfig("mongoHost");
        final Integer mongoPort = getMandatoryIntConfig("mongoPort");
        final String mongoDbName = getMandatoryStringConfig("mongoDbName");

        eb.registerHandler("dorm.management.project.persistor",
                new Handler<Message<JsonObject>>() {
                    @Override
                    public void handle(Message<JsonObject> message) {
                        try {
                            String action = message.body().getString("action");
                            switch (action) {
                                case ACTION_LOAD:
                                    message.reply(getJsonProjects(mongoHost, mongoPort, mongoDbName));
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

    private JsonArray getJsonProjects(String mongoHost, int mongoPort, String mongoDbName) throws JsonProcessingException {

        ProjectRepository projectRepository = new ProjectRepository(new BasicMongoDBDataSource(mongoHost, mongoPort, mongoDbName));
        final List<ProjectDocument> allProjectDocument = projectRepository.getAllProjectDocument();
        List<Object> projectMapList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (ProjectDocument projectDocument : allProjectDocument) {
            projectMapList.add(objectMapper.convertValue(projectDocument, Map.class));
        }

        return new JsonArray(projectMapList);

    }
}
