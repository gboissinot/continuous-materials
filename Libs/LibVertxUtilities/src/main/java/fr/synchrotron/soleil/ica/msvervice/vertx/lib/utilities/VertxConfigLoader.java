package fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoConfigLoader;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;
import java.util.Properties;

/**
 * @author Gregory Boissinot
 */
public class VertxConfigLoader {

    public JsonObject createConfig(JsonObject runConfig) {

        //Load MongoDB information if not provided at launch
        Properties properties = new MongoConfigLoader().loadInfraFile(MongoConfigLoader.MONGODB_DEFAULT_PROPERTIES_FILEPATH);
        for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
            String propKey = (String) objectObjectEntry.getKey();
            if (!runConfig.containsField(propKey)) {
                String propValue = (String) objectObjectEntry.getValue();
                runConfig.putString(propKey, propValue);
            }
        }

        return runConfig;
    }

}
