package seng202.team3.data.database.util;

import java.util.ArrayList;
import org.javatuples.Triplet;
import seng202.team3.data.entity.util.EntityType;

/**
 * Implementation of the {@link QueryBuilder query builder}.
 * Used to build query objects
 *
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class QueryBuilderImpl implements QueryBuilder {
    /** Source of the query */
    private EntityType source;

    /** Filters of the query */
    private ArrayList<Triplet<String, String, ComparisonType>> filters = new ArrayList<>();

    /**
     * unused constructor
     */
    public QueryBuilderImpl() {
        // unused
    }

    /** {@inheritDoc} */
    @Override
    public QueryBuilder withSource(EntityType source) {
        this.source = source;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public QueryBuilder withFilter(String field, String criteria, ComparisonType compareMethod) {
        // Store each filter as a 3-tuple
        filters.add(new Triplet<>(field.toLowerCase(), criteria, compareMethod));
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Query build() {
        return new Query(source, filters);
    }

}
