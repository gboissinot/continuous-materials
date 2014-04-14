package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * @author Gregory Boissinot
 */
public class ArtifactDocumentSerializer implements JsonSerializer<ArtifactDocument> {

    @Override
    public JsonElement serialize(ArtifactDocument artifactDocument, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("org", context.serialize(artifactDocument.getKey().getOrg()));
        jsonObject.add("name", context.serialize(artifactDocument.getKey().getName()));
        jsonObject.add("version", context.serialize(artifactDocument.getKey().getVersion()));
        jsonObject.add("status", context.serialize(artifactDocument.getKey().getStatus()));

        jsonObject.add("type", context.serialize(artifactDocument.getType()));
        jsonObject.add("isThirdParty", context.serialize(artifactDocument.isThirdParty()));

        jsonObject.add("creationDate", context.serialize(artifactDocument.getCreationDate()));
        jsonObject.add("publicationDate", context.serialize(artifactDocument.getPublicationDate()));
        jsonObject.add("sha1", context.serialize(artifactDocument.getSha1()));
        jsonObject.add("md5", context.serialize(artifactDocument.getMd5()));
        jsonObject.add("description", context.serialize(artifactDocument.getDescription()));
        jsonObject.add("fileExtension", context.serialize(artifactDocument.getFileExtension()));
        jsonObject.add("isForce", context.serialize(artifactDocument.isForce()));
        jsonObject.add("javaLanguage", context.serialize(artifactDocument.getJavaLanguage()));
        jsonObject.add("cLanguage", context.serialize(artifactDocument.getcLanguage()));
        jsonObject.add("dependencies", context.serialize(artifactDocument.getDependencies()));
        jsonObject.add("buildContext", context.serialize(artifactDocument.getBuildContext()));

        return jsonObject;
    }
}
