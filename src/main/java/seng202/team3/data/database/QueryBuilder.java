package seng202.team3.data.database;

/**
 * Builder pattern to create custom query objects
 */
public interface QueryBuilder {
    /**
     * Add new source to the query
     * 
     * @param source string representation of the source
     * @return QueryBuilder with a defined source
     */
    QueryBuilder withSource(String source);

    /**
     * Build into a proper query object
     * 
     * @return new Query object
     */
    Query build();
}