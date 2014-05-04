package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.dictionary;

/**
 * @author Gregory Boissinot
 */
public class NoDictionary extends Dictionary {

    @Override
    public String getDictionaryFilePath() {
        return null;
    }
}
