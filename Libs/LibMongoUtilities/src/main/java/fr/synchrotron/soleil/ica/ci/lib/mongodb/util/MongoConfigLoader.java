package fr.synchrotron.soleil.ica.ci.lib.mongodb.util;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Gregory Boissinot
 */
public class MongoConfigLoader {

    public static final String MONGODB_DEFAULT_PROPERTIES_FILEPATH = "/infra.properties";

    public Properties loadInfraFile(String propertiedFilePath) {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream(propertiedFilePath));
            return properties;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
