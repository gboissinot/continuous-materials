package fr.synchrotron.soleil.ica.ci.service.dornmanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.repository.ProjectRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.json.JsonArray;

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
        //TODO
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
