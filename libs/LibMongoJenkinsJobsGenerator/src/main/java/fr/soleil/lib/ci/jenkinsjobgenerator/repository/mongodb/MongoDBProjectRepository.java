package fr.soleil.lib.ci.jenkinsjobgenerator.repository.mongodb;

import com.mongodb.DB;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.project.ProjectDocument;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.BasicMongoDBDataSource;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDBProjectRepository {
	private Logger logger = LoggerFactory.getLogger(MongoDBProjectRepository.class);
	public static final String MONGODB_PROJECTS = "projects";
	public static final String MONGODB_NAME = "repo";
	private Jongo jongo;
	
	public MongoDBProjectRepository(String mongoHost, int mongoPort) {
		BasicMongoDBDataSource mongoDBDatasource = new BasicMongoDBDataSource(
				mongoHost, mongoPort, MONGODB_NAME);

		DB mongoDB = mongoDBDatasource.getMongoDB();
		jongo = new Jongo(mongoDB);
		//DBCollection coll = mongoDB.getCollection(MONGODB_PROJECTS);
		//logger.debug("there are {} project in DB", coll.count());
	}
	
	public Iterable<ProjectDocument> loadProjects() {
		MongoCollection collection = jongo.getCollection(MONGODB_PROJECTS);
		return collection.find().as(ProjectDocument.class);
	}

	/**
	 * TODO remove. Just for tests
	 * 
	 * @param projectDocument
	 */
//	public void insertProjectDocument(ProjectDocument projectDocument) {
//		MongoCollection projects = jongo.getCollection(MONGODB_PROJECTS);
//		projects.insert(projectDocument);
//	}
//
//	public static void main(String[] args) {
//        MongoDBProjectRepository loader = new MongoDBProjectRepository("172.16.5.7", 27001);
//		// insert document 1
//		ProjectDocument projectDocument = new ProjectDocument();
//		projectDocument.setDescription("desc");
//		projectDocument.setName("test1");
//		projectDocument.setOrg("fr.soleil.test");
//		projectDocument
//				.setScmConnection("scm:cvs:pserver:anonymous:@ganymede.synchrotron-soleil.fr:/usr/local/CVS:DeviceServer/Generic/Tests/ErrorGenerator");
//		List<DeveloperDocument> developers = new ArrayList<DeveloperDocument>();
//		DeveloperDocument dev1 = new DeveloperDocument();
//		dev1.setId("id");
//		dev1.setEmail("toto@soleil.fr");
//		dev1.setName("toto");
//		developers.add(dev1);
//		DeveloperDocument dev2 = new DeveloperDocument();
//		dev2.setId("id2");
//		dev2.setEmail("titi@soleil.fr");
//		dev2.setName("titi");
//		developers.add(dev2);
//		projectDocument.setDevelopers(developers);
//        projectDocument.setLanguage("java");
//		loader.insertProjectDocument(projectDocument);
//		// insert document 2
//		ProjectDocument projectDocument3 = new ProjectDocument();
//		projectDocument3.setDescription("desc2");
//		projectDocument3.setName("test2");
//		projectDocument3.setOrg("fr.soleil.test2");
//		projectDocument3
//				.setScmConnection("scm:svn:http://svn.code.sf.net/p/cometeapps/code/TangoBeans/ScanServer/CurrentScanResultBean/trunk");
//		List<DeveloperDocument> developers2 = new ArrayList<DeveloperDocument>();
//		DeveloperDocument dev3 = new DeveloperDocument();
//		dev3.setId("id3");
//		dev3.setEmail("doudou@soleil.fr");
//		dev3.setName("doudou");
//		developers2.add(dev3);
//		projectDocument3.setDevelopers(developers2);
//        projectDocument.setLanguage("java");
//		loader.insertProjectDocument(projectDocument3);
//		// Iterable<ProjectDocument> iterable = loader.loadProjects();
//		// for (ProjectDocument projectDocument2 : iterable) {
//		// System.out.println(projectDocument2);
//		// }
//	}

}
