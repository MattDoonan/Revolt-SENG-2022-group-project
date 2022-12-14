package seng202.team3.data.database.util;

import java.io.IOException;
import java.util.List;
import seng202.team3.data.entity.Entity;

/**
 * Interface for objects that interact with raw data storage
 *
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public interface DataReader {
    /**
     * Queries data from the relevant data source
     *
     * @param query data request
     * @return List of stored entities
     * @throws java.io.IOException file cannot be read
     */
    public List<Entity> readData(Query query)
            throws IOException;
}
