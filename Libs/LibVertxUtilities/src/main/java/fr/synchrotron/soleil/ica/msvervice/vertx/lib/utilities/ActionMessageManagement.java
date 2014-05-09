package fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities;

import org.vertx.java.core.VertxException;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonElement;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class ActionMessageManagement {

    private static final String ACTION_NAME = "action";
    private static final String DOCUMENT_NAME = "document";

    public JsonObject createMessage(String actionName, Object content) {

        if (actionName == null) {
            throw new NullPointerException("An action name is required.");
        }

        if (content == null) {
            throw new NullPointerException("A content is required.");
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.putString(ACTION_NAME, actionName);

        if (content instanceof JsonElement) {
            jsonObject.putElement(DOCUMENT_NAME, (JsonElement) content);
        }

        if (content instanceof String) {
            jsonObject.putString(DOCUMENT_NAME, (String) content);
        }

        return jsonObject;
    }

    private void checkDormMessage(String actionName, Message message) {
        final Object body = message.body();
        if (!(body instanceof JsonObject)) {
            throw new VertxException("All DORM objects through event bus must be JSObject.");
        }

        JsonObject jsonObject = (JsonObject) body;

        final String action = jsonObject.getString(ACTION_NAME);
        if (!actionName.equals(action)) {
            throw new VertxException(String.format("Action %s is expected.", actionName));
        }
    }

    public JsonElement getJsonElementDocument(String actionName, Message message) {

        if (actionName == null) {
            throw new NullPointerException("An action name is required.");
        }

        if (message == null) {
            throw new NullPointerException("A message is required.");
        }

        final Object documentObject = getJsonObject(actionName, message).getValue(DOCUMENT_NAME);
        if (!(documentObject instanceof JsonElement)) {
            throw new VertxException("A Vert.x JsonElement as document is expected.");
        }

        return (JsonElement) documentObject;
    }

    public Map<String, Object> getMapDocument(String actionName, Message message) {

        if (actionName == null) {
            throw new NullPointerException("An action name is required.");
        }

        if (message == null) {
            throw new NullPointerException("A message is required.");
        }

        final Object documentObject = getJsonObject(actionName, message).getValue(DOCUMENT_NAME);
        if (!(documentObject instanceof JsonObject)) {
            throw new VertxException("A Vert.x JsonObject as document is expected.");
        }

        final JsonObject jsonObject = (JsonObject) documentObject;
        return jsonObject.toMap();
    }

    public String getStringDocument(String actionName, Message message) {

        if (actionName == null) {
            throw new NullPointerException("An action name is required.");
        }

        if (message == null) {
            throw new NullPointerException("A message is required.");
        }

        final Object documentObject = getJsonObject(actionName, message).getValue(DOCUMENT_NAME);
        if (!(documentObject instanceof String)) {
            throw new VertxException("A String as document is expected.");
        }

        return (String) documentObject;
    }

    private JsonObject getJsonObject(String actionName, Message message) {
        checkDormMessage(actionName, message);
        final Object body = message.body();
        return (JsonObject) body;
    }

}
