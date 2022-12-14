package seng202.team3.data.database.util;

import java.util.List;
import org.javatuples.Triplet;
import seng202.team3.data.entity.util.EntityType;

/**
 * Represents a request to retrieve information from data storage
 *
 * @author Harrison Tyson
 * @version 1.0.1, Aug 22
 */
public class Query {
    /** Location to get information from */
    private EntityType source;

    /**
     * Filter criteria for the query
     * In the form: Triplet(Field, Criteria, ComparisonMethod)
     */
    List<Triplet<String, String, ComparisonType>> filters;

    /**
     * Constructor for the query
     *
     * @param source  Location to get information from
     * @param filters Filter criteria for the query
     */
    public Query(EntityType source, List<Triplet<String, String, ComparisonType>> filters) {
        this.source = source;
        this.filters = filters;
    }

    /**
     * Get source location for the data
     *
     * @return source of the query
     */
    public EntityType getSource() {
        return source;
    }

    /**
     * Get the list of filters for the query
     *
     * @return list of filters for the query
     */
    public List<Triplet<String, String, ComparisonType>> getFilters() {
        return filters;
    }

}
