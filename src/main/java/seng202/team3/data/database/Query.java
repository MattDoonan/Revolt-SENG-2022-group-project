package seng202.team3.data.database;

/**
 * Represents a request to retrieve information from data storage
 * 
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class Query {
    /** Location to get information from */
    private String source;

    /** Constructor for the query */
    public Query(String source) {
        this.source = source;
    }

    /**
     * String format to be processed
     * 
     * @return source of the query
     */
    @Override
    public String toString() {
        return source;
    }
}