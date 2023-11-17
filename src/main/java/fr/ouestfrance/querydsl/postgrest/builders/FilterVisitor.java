package fr.ouestfrance.querydsl.postgrest.builders;

/**
 * Filter Visitor to transform filter to queryString
 */
public interface FilterVisitor {

    /**
     * Visitor function
     *
     * @param visitor visitor to handle.
     */
    void accept(QueryFilterVisitor visitor);

    /**
     * Get the filter string representation
     * @return the filterString
     */
    default String getFilterString() {
        QueryFilterVisitor visitor = new QueryFilterVisitor();
        accept(visitor);
        return visitor.getValue();
    }
}