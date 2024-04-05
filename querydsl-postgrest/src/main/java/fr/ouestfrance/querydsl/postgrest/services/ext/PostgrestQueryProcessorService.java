package fr.ouestfrance.querydsl.postgrest.services.ext;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.model.GroupFilter;
import fr.ouestfrance.querydsl.postgrest.mappers.*;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.CompositeFilter;
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
            new LikeMapper(), new NotEqualsMapper(), new NotInMapper(),
            new CaseInsensitiveLikeMapper(), new ContainsMapper(), new ContainedMapper(),
            new IsNullMapper(), new RangeMapper());

    @Override
    public Mapper<Filter> getMapper(Class<? extends FilterOperation> operation) {
        return MAPPERS.stream()
                .filter(x -> x.operation().equals(operation))
                .findFirst().orElseThrow();
    }

    @Override
    public Filter group(List<Filter> filters, GroupFilter.Operand operand) {
        return switch (operand) {
            case OR -> CompositeFilter.of("or", filters);
            case AND -> CompositeFilter.of("and", filters);
        };
    }
}
