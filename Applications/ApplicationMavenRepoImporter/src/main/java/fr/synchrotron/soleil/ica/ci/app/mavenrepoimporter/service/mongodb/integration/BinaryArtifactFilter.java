package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.mongodb.integration;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import org.springframework.integration.annotation.Filter;

/**
 * @author Gregory Boissinot
 */
public class BinaryArtifactFilter {

    @Filter
    @SuppressWarnings("unused")
    public boolean filteringBinaryArtifacts(ArtifactDocument artifactObj) {

//        final String org = artifactObj.getOrg();
//        final String name = artifactObj.getName();
//        final String version = artifactObj.getVersion();
//        if ("org.codehaus.service-conduit".equals(org)
//                && "sca4j-xmlfactory".equals(name)
//                && "0.9.6".equals(version) ) {
//            return true;
//        } else {
//            return false;
//        }
//
////        "org" : "yan",
////       	"name" : "yan",
////
        return ("binary".equals(artifactObj.getType()));
    }
}
