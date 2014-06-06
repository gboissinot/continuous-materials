package fr.synchrotron.soleil.ica.ci.lib.mongodb.pomimporter.service.dictionary;

/**
 * @author Gregory Boissinot
 */
public class SoleilDictionary extends Dictionary {

    @Override
    public String getDictionaryFilePath() {
        return "./soleil-dictionary.properties";
    }
}
