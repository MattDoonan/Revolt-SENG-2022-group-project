package seng202.team3.data.database;

/**
 * Builder pattern to create custom query objects
 * 
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
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
     * Add a new filter criteria to the query.
     * Multiple filters act as a logical AND
     * 
     * @param field         name of the field to filter on
     * @param criteria      criteria to apply to the field
     * @param compareMethod enum specifying how criteria should be compared
     * @return QueryBuilder with an additional filter
     */
    QueryBuilder withFilter(String field, String criteria, ComparisonType compareMethod);

    /**
     * Remove filter from the query. If the filter is not present
     * do nothing
     * 
     * @param field name of the field to remove from the query
     * @return QueryBuilder without the filter
     */
    QueryBuilder withoutFilter(String field);

    /**
     * Build into a proper query object
     * 
     * @return new Query object
     */
    Query build();
}