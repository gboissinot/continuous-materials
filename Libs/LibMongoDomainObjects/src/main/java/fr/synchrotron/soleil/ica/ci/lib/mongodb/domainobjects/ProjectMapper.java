package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Gregory Boissinot
 */
public class ProjectMapper  extends ObjectMapper {

    public ProjectMapper() {
        registerModule(new ProjectModule());
    }
}
