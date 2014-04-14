package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects;

import com.fasterxml.jackson.databind.module.SimpleModule;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;


/**
 * @author Gregory Boissinot
 */
public class ProjectModule extends SimpleModule {

    public ProjectModule() {
        addSerializer(ProjectDocument.class, new ProjectDocumentSerializer());
        addDeserializer(ProjectDocument.class, new ProjectDocumentDeSerializer());
    }
}
