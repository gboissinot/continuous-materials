package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * @author Gregory Boissinot
 */
public class GsonProjectDocumentSerializer implements JsonSerializer<ProjectDocument> {

    @Override
    public JsonElement serialize(ProjectDocument projectDocument, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("org", context.serialize(projectDocument.getKey().getOrg()));
        jsonObject.add("name", context.serialize(projectDocument.getKey().getName()));
        jsonObject.add("developers", context.serialize(projectDocument.getDevelopers()));
        jsonObject.add("description", context.serialize(projectDocument.getDescription()));
        jsonObject.add("scmConnection", context.serialize(projectDocument.getScmConnection()));
        jsonObject.add("language", context.serialize(projectDocument.getLanguage()));

        return jsonObject;
    }
}
