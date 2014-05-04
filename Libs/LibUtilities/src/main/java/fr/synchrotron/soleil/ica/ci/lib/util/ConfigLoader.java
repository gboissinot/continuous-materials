package fr.synchrotron.soleil.ica.ci.lib.util;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Gregory Boissinot
 */
public class ConfigLoader {

    public Properties loadPropertiesFile(String propertiedFilePath) {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream(propertiedFilePath));
            return properties;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
