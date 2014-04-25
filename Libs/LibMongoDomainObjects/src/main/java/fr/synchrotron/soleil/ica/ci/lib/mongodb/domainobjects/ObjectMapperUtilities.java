package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocumentKey;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocumentKeySerializer;

/**
 * @author Gregory Boissinot
 */
public class ObjectMapperUtilities {

    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        module.addSerializer(ArtifactDocumentKey.class, new ArtifactDocumentKeySerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
