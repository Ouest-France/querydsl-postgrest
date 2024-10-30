package fr.ouestfrance.querydsl.postgrest.utils;

import fr.ouestfrance.querydsl.postgrest.annotations.Select;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.CompositeFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SelectFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SelectFilter.Attribute;

import java.util.*;

public class FilterUtils {


    private static final String AND = "and";

    /**
     * Get attributes from objects
     * @param objects objects to scan
     * @return list of select attributes
     */
    public static List<Attribute> getSelectAttributes(Object... objects) {
        List<SelectFilter.Attribute> attributes = new ArrayList<>();
        if(objects == null) {
            return attributes;
        }
        for (Object object : objects) {
            if(object != null) {
                Select[] clazzAnnotation = object.getClass().getAnnotationsByType(Select.class);
                if (clazzAnnotation.length > 0) {
                    attributes.addAll(Arrays.stream(clazzAnnotation).map(x -> new SelectFilter.Attribute(x.alias(), x.value(), x.only())).toList());
                }
            }
        }
        return attributes;
    }

    /**
     * Transform a filter list to map of queryString
     *
     * @param filters list of filters
     * @return map of query strings
     */
    public static Map<String, List<String>> toMap(List<Filter> filters) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        filters.forEach(x -> {
            // If filter is an "and" with the same keys, then we decompose it and transform it to filter list
            if (x instanceof CompositeFilter compositeFilter && AND.equals(compositeFilter.getKey()) &&
                compositeFilter.getFilters().stream().map(Filter::getKey).distinct().count() == 1) {
                for (Filter filter : compositeFilter.getFilters()) {
                    map.computeIfAbsent(filter.getKey(), key -> new ArrayList<>()).add(filter.getFilterString());
                }
            } else {
                map.computeIfAbsent(x.getKey(), key -> new ArrayList<>()).add(x.getFilterString());
            }
        });
        return map;
    }
}
