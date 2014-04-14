package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact;

import org.jongo.Mapper;
import org.jongo.ObjectIdUpdater;
import org.jongo.marshall.Marshaller;
import org.jongo.marshall.Unmarshaller;
import org.jongo.query.QueryFactory;

/**
 * @author Gregory Boissinot
 */
public class GsonMapper implements Mapper {
    @Override
    public Marshaller getMarshaller() {
        return null;
    }

    @Override
    public Unmarshaller getUnmarshaller() {
        return null;
    }

    @Override
    public ObjectIdUpdater getObjectIdUpdater() {
        return null;
    }

    @Override
    public QueryFactory getQueryFactory() {
        return null;
    }
}
