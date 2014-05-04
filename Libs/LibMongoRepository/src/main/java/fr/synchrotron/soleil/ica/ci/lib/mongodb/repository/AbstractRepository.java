package fr.synchrotron.soleil.ica.ci.lib.mongodb.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.ObjectMapperUtilities;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBDataSource;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.util.MongoDBException;
import org.jongo.Jongo;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @author Gregory Boissinot
 */
abstract class AbstractRepository {

    protected Jongo jongo;

    AbstractRepository(MongoDBDataSource mongoDBDataSource) {
        DB mongoDB = mongoDBDataSource.getMongoDB();
        jongo = new Jongo(mongoDB);
    }

    String getStringQuery(Object queryObject) {

        ObjectMapperUtilities objectMapperUtilities = new ObjectMapperUtilities();
        final ObjectMapper objectMapper = objectMapperUtilities.getObjectMapper();
        StringWriter stringWriter = new StringWriter();
        try {
            objectMapper.writeValue(stringWriter, queryObject);
        } catch (IOException ioe) {
            throw new MongoDBException(ioe);
        }

        return stringWriter.toString();
    }

}
