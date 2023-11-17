package fr.ouestfrance.querydsl.postgrest.model;

import fr.ouestfrance.querydsl.postgrest.builders.FilterVisitor;

/**
 * Interface that represent a filter
 */
public interface Filter extends FilterVisitor {

    /**
     * Get the filter key
     * @return filter key
     */
    String getKey();

}