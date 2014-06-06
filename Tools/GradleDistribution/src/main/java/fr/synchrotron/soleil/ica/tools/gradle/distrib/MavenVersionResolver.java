package fr.synchrotron.soleil.ica.tools.gradle.distrib;

import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.repository.mongodb.MongoDBArtifactRepository;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service.ArtifactVersionResolverService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.latestversionrresolver.service.MavenVersionResolverService;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoConfigLoader;

import java.util.Properties;

/**
 * @author Gregory Boissinot
 */
public class MavenVersionResolver {

    public String getLatestVersion(String group, String name) {
        MongoConfigLoader mongoConfigLoader = new MongoConfigLoader();
        final Properties loadInfraFile = mongoConfigLoader.loadInfraFile(MongoConfigLoader.MONGODB_DEFAULT_PROPERTIES_FILEPATH);
        final BasicMongoDBDataSource mongoDBDatasource = new BasicMongoDBDataSource(loadInfraFile);
        final MongoDBArtifactRepository artifactRepository = new MongoDBArtifactRepository(mongoDBDatasource);
        final ArtifactVersionResolverService artifactVersionResolverService = new ArtifactVersionResolverService(artifactRepository);
        MavenVersionResolverService resolverService = new MavenVersionResolverService(artifactVersionResolverService);
        return resolverService.getLatestArtifact(group, name);
    }

}
