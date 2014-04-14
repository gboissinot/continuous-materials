package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ext.DeveloperDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;

import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public class ProjectDocumentDeSerializer extends JsonDeserializer<ProjectDocument> {

    @Override
    public ProjectDocument deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        /*
                jgen.writeStringField("org", projectDocument.getKey().getOrg());
        jgen.writeStringField("name", projectDocument.getKey().getName());
        jgen.writeObjectField("developers", projectDocument.getDevelopers());
        jgen.writeStringField("description", projectDocument.getDescription());
        jgen.writeStringField("scmConnection", projectDocument.getScmConnection());
        jgen.writeStringField("language", projectDocument.getLanguage());

//         */
//        jp.nextToken();
//        String org = jp.getValueAsString();
//        jp.nextToken();
//        String name = jp.getValueAsString();
//        ProjectDocument projectDocument = new ProjectDocument(org, name);
//        jp.nextToken();
//        //List<DeveloperDocument>  developerDocuments = (List<DeveloperDocument>)jp.getEmbeddedObject();

        //jp.getText();

        //jp.nextToken();


        ObjectCodec oc = jsonParser.getCodec();
        System.out.println(oc);
        JsonNode node = oc.readTree(jsonParser);
        System.out.println(node);

        final JsonNode org = node.get("org");
        System.out.println(org);
        ProjectDocument projectDocument = new ProjectDocument(
                org.textValue(), node.get("name").textValue()
        );

        projectDocument.setDescription(node.get("description").asText());

        final JsonNode developers = node.get("developers");
        if (developers instanceof ArrayNode){
            ArrayNode developersArrayNode = (ArrayNode)developers;
            ObjectMapper objectMapper = new ObjectMapper();
            final DeveloperDocument developers1 = objectMapper.readValue(node.get("developers").asText(), DeveloperDocument.class);
        }
//        projectDocument.setDevelopers(developers);

        return projectDocument;
    }
}
