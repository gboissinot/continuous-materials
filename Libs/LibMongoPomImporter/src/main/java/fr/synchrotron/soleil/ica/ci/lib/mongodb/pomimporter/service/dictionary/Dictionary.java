package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.dictionary;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.exception.POMImporterException;
import fr.synchrotron.soleil.ica.ci.lib.util.ConfigLoader;
import org.apache.commons.lang.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Gregory Boissinot
 */
public abstract class Dictionary {

    private Map<String, String> content;

    protected Dictionary() {
        content = new HashMap<String, String>();
        final String dictionaryFilePath = getDictionaryFilePath();
        if (dictionaryFilePath != null) {
            try {
                ConfigLoader configLoader = new ConfigLoader();
                Properties properties = configLoader.loadPropertiesFile(dictionaryFilePath);
                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    content.put((String) entry.getKey(), (String) entry.getValue());
                }

            } catch (Throwable e) {
                throw new POMImporterException("Can't create the importation dictionary.");
            }
        }
    }

    public String resolve(String value) {
        StrSubstitutor sub = new StrSubstitutor(content);
        return sub.replace(value);
    }

    public abstract String getDictionaryFilePath();

}
