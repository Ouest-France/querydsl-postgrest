package fr.ouestfrance.querydsl.postgrest.services.ext;

import fr.ouestfrance.querydsl.postgrest.mappers.*;
import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.mappers.*;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.service.ext.Mapper;
import fr.ouestfrance.querydsl.service.ext.QueryDslProcessorService;

import java.util.List;

/**
 * Concrete implementation of QueryDslProcessorService
 */
public class PostgrestQueryProcessorService implements QueryDslProcessorService<Filter> {

    /**
     * List of mappers
     */
    private static final List<Mapper<Filter>> MAPPERS = List.of(new EqualsMapper(),
            new GreaterThanEqualsMapper(), new GreaterThanMapper(),
            new InMapper(), new LessThanEqualsMapper(), new LessThanMapper(),
            new LikeMapper(), new NotEqualsMapper(), new NotInMapper());

    @Override
    public Mapper<Filter> getMapper(FilterOperation operation) {
        return MAPPERS.stream()
                .filter(x -> x.operation().equals(operation))
                .findFirst().orElseThrow();
    }
}
