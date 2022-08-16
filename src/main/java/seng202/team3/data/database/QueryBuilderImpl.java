package seng202.team3.data.database;

/**
 * Implementation of the {@link QueryBuilder query builder}.
 * Used to build query objects
 * 
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class QueryBuilderImpl implements QueryBuilder {
    /** Source of the query */
    private String source;

    @Override
    public QueryBuilder withSource(String source) {
        this.source = source;
        return this;
    }

    @Override
    public Query build() {
        return new Query(source);
    }
}
