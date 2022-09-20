package seng202.team3.data.database;

import java.io.IOException;
import java.util.List;

/**
 * Interface for objects that interact with raw data storage
 *
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public interface DataManager {
    /**
     * Queries data from the relevant data source
     *
     * @param query               data request
     * @param objectToInterpretAs entity to convert data into
     * @return ArrayList of string records
     * @throws java.io.IOException file cannot be read
     */
    public List<Object> readData(Query query, Class<?> objectToInterpretAs)
            throws IOException;
}
