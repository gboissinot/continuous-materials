package fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities;

import org.vertx.java.core.json.JsonElement;
import org.vertx.java.core.json.JsonObject;

/**
 * @author Gregory Boissinot
 */
public class DORMActionMessage {

    private static final String ACTION_NAME = "action";
    private static final String DOCUMENT_NAME = "document";

    public JsonObject createActionMessage(String actionName, Object content) {

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

}
