package seng202.team3.data.database;

import java.util.ArrayList;

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
     * @param query data request
     * 
     * @return ArrayList of string records
     */
    public ArrayList<String> readData(Query query);
}
