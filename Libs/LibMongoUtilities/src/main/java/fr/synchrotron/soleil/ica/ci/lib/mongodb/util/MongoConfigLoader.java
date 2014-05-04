package fr.synchrotron.soleil.ica.ci.lib.mongodb.util;

import fr.synchrotron.soleil.ica.ci.lib.util.ConfigLoader;

import java.util.Properties;

/**
 * @author Gregory Boissinot
 */
public class MongoConfigLoader {

    public static final String MONGODB_DEFAULT_PROPERTIES_FILEPATH = "/infra.properties";

    public Properties loadInfraFile(String propertiedFilePath) {
        ConfigLoader configLoader = new ConfigLoader();
        return configLoader.loadPropertiesFile(propertiedFilePath);
    }
}
