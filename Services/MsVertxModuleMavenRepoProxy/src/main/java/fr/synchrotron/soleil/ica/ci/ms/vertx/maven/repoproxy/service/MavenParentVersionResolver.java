package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.service;


import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.ArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service.MavenVersionResolverService;

/**
 * @author Gregory Boissinot
 */
public class MavenParentVersionResolver {

    private ArtifactRepository artifactRepository;

    public MavenParentVersionResolver(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    public String resolveParentVersion(String parentGroupId, String parentArtifactId) {
        MavenVersionResolverService versionResolverService
                = new MavenVersionResolverService(artifactRepository);
        return versionResolverService.getLatestArtifact(parentGroupId, parentArtifactId);
    }
}
