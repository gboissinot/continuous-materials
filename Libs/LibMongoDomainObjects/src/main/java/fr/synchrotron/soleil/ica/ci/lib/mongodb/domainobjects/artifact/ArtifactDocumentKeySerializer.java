package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public class ArtifactDocumentKeySerializer extends JsonSerializer<ArtifactDocumentKey> {

    @Override
    public void serialize(ArtifactDocumentKey artifactDocumentKey, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField("org", artifactDocumentKey.getOrg());
        jgen.writeStringField("name", artifactDocumentKey.getName());
        jgen.writeStringField("version", artifactDocumentKey.getVersion());
        jgen.writeStringField("status", artifactDocumentKey.getStatus());
        jgen.writeEndObject();
    }

}
