package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;

import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public class ProjectDocumentSerializer extends com.fasterxml.jackson.databind.JsonSerializer<ProjectDocument> {

    @Override
    public void serialize(ProjectDocument projectDocument,
                          JsonGenerator jgen,
                          SerializerProvider provider) throws IOException, JsonProcessingException {


        jgen.writeStartObject();

        jgen.writeStringField("org", projectDocument.getKey().getOrg());
        jgen.writeStringField("name", projectDocument.getKey().getName());
        jgen.writeObjectField("developers", projectDocument.getDevelopers());
        jgen.writeStringField("description", projectDocument.getDescription());
        //jgen.writeStringField("scmConnection", projectDocument.getScmConnection());
        //jgen.writeStringField("language", projectDocument.getLanguage());

        jgen.writeEndObject();

    }
}
