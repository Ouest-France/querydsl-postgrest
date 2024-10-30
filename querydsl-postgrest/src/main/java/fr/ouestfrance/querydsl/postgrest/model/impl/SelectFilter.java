package fr.ouestfrance.querydsl.postgrest.model.impl;

import fr.ouestfrance.querydsl.postgrest.builders.FilterVisitor;
import fr.ouestfrance.querydsl.postgrest.builders.QueryFilterVisitor;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Select filter allow to describe a selection
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SelectFilter implements Filter, FilterVisitor {

    /**
     * Default query param key for selection
     */
    private static final String KEY_PARAMETER = "select";
    /**
     * alias
     */
    private List<Attribute> selectAttributes;
    private boolean addAll;

    /**
     * Create select filter from embedded resources
     *
     * @param selectAttributes selectAttributes of the selection (can be empty)
     * @return select filter
     */
    public static Filter of(List<Attribute> selectAttributes) {
        return new SelectFilter(selectAttributes, !isOnly(selectAttributes));
    }

    public static Filter only(List<Attribute> selectAttributes) {
        return new SelectFilter(selectAttributes, false);
    }

    private static boolean isOnly(List<Attribute> selectAttributes) {
        if(selectAttributes == null || selectAttributes.isEmpty()) {
            return false;
        }
        return selectAttributes.stream().findFirst().map(Attribute::isOnlyAttribute).orElse(false);
    }

    public Filter append(List<Attribute> selectAttributes) {
        if(selectAttributes == null || selectAttributes.isEmpty()) {
            return this;
        }
        this.selectAttributes.addAll(selectAttributes);
        return this;
    }

    @Override
    public void accept(QueryFilterVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getKey() {
        return KEY_PARAMETER;
    }


    /**
     * Attribute name
     */
    @Getter
    @RequiredArgsConstructor
    public static class Attribute {
        /**
         * alias
         */
        private final String alias;
        /**
         * value selected
         */
        private final String[] value;

        /**
         * only attribute
         */
        private final boolean onlyAttribute;

    }
}
