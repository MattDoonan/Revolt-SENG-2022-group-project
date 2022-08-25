package seng202.team3.data.database;

import java.util.ArrayList;
import org.javatuples.Triplet;

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

    /** Filters of the query */
    private ArrayList<Triplet<String, String, ComparisonType>> filters = new ArrayList<>();

    @Override
    public QueryBuilder withSource(String source) {
        this.source = source;
        return this;
    }

    @Override
    public QueryBuilder withFilter(String field, String criteria, ComparisonType compareMethod) {
        // Store each filter as a 3-tuple
        filters.add(new Triplet<>(field, criteria, compareMethod));
        return this;
    }

    @Override
    public QueryBuilder withoutFilter(String field) {
        for (Triplet<String, String, ComparisonType> filter : filters) {
            if (filter.getValue0().toLowerCase() == field.toLowerCase()) {
                filters.remove(filter);
                break;
            }
        }
        return this;
    }

    @Override
    public Query build() {
        return new Query(source, filters);
    }

}
