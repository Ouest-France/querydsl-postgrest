package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.annotations.Header;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.PageImpl;
import fr.ouestfrance.querydsl.postgrest.model.Pageable;
import fr.ouestfrance.querydsl.postgrest.model.RangeResponse;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.MissingConfigurationException;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import fr.ouestfrance.querydsl.postgrest.model.impl.OrderFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SelectFilter;
import fr.ouestfrance.querydsl.postgrest.services.ext.PostgrestQueryProcessorService;
import fr.ouestfrance.querydsl.service.ext.QueryDslProcessorService;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Postgrest repository implementation
 *
 * @param <T> type of returned entity
 */
public class PostgrestRepository<T> implements Repository<T> {

    private final QueryDslProcessorService<Filter> processorService = new PostgrestQueryProcessorService();
    private final PostgrestConfiguration annotation;
    private final Map<Header.Method, Map<String, List<String>>> headersMap = new EnumMap<>(Header.Method.class);
    private final PostgrestClient client;
    private Class<T> clazz;

    /**
     * Postgrest Repository constructor
     *
     * @param client webClient adapter
     */
    public PostgrestRepository(PostgrestClient client) {
        this.client = client;
        if (!getClass().isAnnotationPresent(PostgrestConfiguration.class)) {
            throw new MissingConfigurationException(getClass(),
                    "Missing annotation " + PostgrestConfiguration.class.getSimpleName());
        }
        annotation = getClass().getAnnotation(PostgrestConfiguration.class);
        // Create headerMap
        Arrays.stream(getClass().getAnnotationsByType(Header.class))
                .forEach(header -> Arrays.stream(header.methods())
                        .forEach(method ->
                                headersMap.computeIfAbsent(method, x -> new LinkedHashMap<>())
                                        .computeIfAbsent(header.key(), x -> new ArrayList<>())
                                        .addAll(Arrays.asList(header.value()))
                        )
                );

        if (getClass().getGenericSuperclass() instanceof ParameterizedType type) {
            //noinspection unchecked
            clazz = (Class<T>) type.getActualTypeArguments()[0];
        }

    }

    @Override
    public Page<T> search(Object criteria, Pageable pageable) {
        List<Filter> queryParams = processorService.process(criteria);
        Map<String, List<String>> headers = headerMap(Header.Method.GET);
        // Add pageable if present
        if (pageable.getPageSize() > 0) {
            headers.put("Range-Unit", List.of("items"));
            headers.put("Range", List.of(pageable.toRange()));
            headers.computeIfAbsent("Prefer", x -> new ArrayList<>())
                    .add("count=" + annotation.countStrategy().name().toLowerCase());
        }
        // Add sort if present
        if (pageable.getSort() != null) {
            queryParams.add(OrderFilter.of(pageable.getSort()));
        }
        // Add select criteria
        getSelects(criteria).ifPresent(queryParams::add);
        RangeResponse<T> response = client.search(annotation.resource(), toMap(queryParams), headers, clazz);

        int pageSize = Optional.of(pageable)
                .filter(Pageable::hasSize)
                .map(Pageable::getPageSize)
                .orElse(response.getPageSize());
        // Compute PageResponse
        return new PageImpl<>(response.data(), pageable, response.getTotalElements(), (int) Math.ceil((double) response.getTotalElements() / pageSize));
    }

    @Override
    public List<T> upsert(List<Object> values) {
        return client.post(annotation.resource(), values, headerMap(Header.Method.UPSERT), clazz);
    }


    @Override
    public List<T> patch(Object criteria, Object body) {
        List<Filter> queryParams = processorService.process(criteria);
        // Add select criteria
        getSelects(criteria).ifPresent(queryParams::add);
        return client.patch(annotation.resource(), toMap(queryParams), body, headerMap(Header.Method.UPSERT), clazz);
    }


    @Override
    public List<T> delete(Object criteria) {
        List<Filter> queryParams = processorService.process(criteria);
        // Add select criteria
        getSelects(criteria).ifPresent(queryParams::add);
        return client.delete(annotation.resource(), toMap(queryParams), headerMap(Header.Method.DELETE), clazz);
    }

    /**
     * Transform a filter list to map of queryString
     *
     * @param filters list of filters
     * @return map of query strings
     */
    private Map<String, List<String>> toMap(List<Filter> filters) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        filters.forEach(x -> map.computeIfAbsent(x.getKey(), key -> new ArrayList<>()).add(x.getFilterString()));
        return map;
    }


    /**
     * Extract selection on criteria and class
     *
     * @param criteria search criteria
     * @return attributes
     */
    private Optional<Filter> getSelects(Object criteria) {
        List<SelectFilter.Attribute> attributes = new ArrayList<>();
        Select[] clazzAnnotation = getClass().getAnnotationsByType(Select.class);
        if (clazzAnnotation.length > 0) {
            attributes.addAll(Arrays.stream(clazzAnnotation).map(x -> new SelectFilter.Attribute(x.alias(), x.value())).toList());
        }
        if (criteria != null) {
            Select[] criteriaAnnotation = criteria.getClass().getAnnotationsByType(Select.class);
            if (criteriaAnnotation.length > 0) {
                attributes.addAll(Arrays.stream(criteriaAnnotation).map(x -> new SelectFilter.Attribute(x.alias(), x.value())).toList());
            }
        }
        return Optional.of(attributes)
                .filter(x -> !x.isEmpty())
                .map(SelectFilter::of);
    }

    /**
     * Find one object using criteria, method can return one or empty
     *
     * @param criteria search criteria
     * @return Optional result
     * @throws PostgrestRequestException when search criteria result gave more than one item
     */
    public Optional<T> findOne(Object criteria) {
        Page<T> search = search(criteria);
        if (search.getTotalElements() > 1) {
            throw new PostgrestRequestException(annotation.resource(),
                    "Search with params " + criteria + " must found only one result, but found " + search.getTotalElements() + " results");
        }
        return search.stream().findFirst();
    }

    /**
     * Get one object using criteria, method should return the response
     *
     * @param criteria search criteria
     * @return Result object
     * @throws PostgrestRequestException no element found, or more than one item
     */
    public T getOne(Object criteria) {
        return findOne(criteria).orElseThrow(
                () -> new PostgrestRequestException(annotation.resource(),
                        "Search with params " + criteria + " must return one result, but found 0"));
    }

    /**
     * Retrieve headerMap for a specific method
     *
     * @param method method
     * @return header map
     */
    private Map<String, List<String>> headerMap(Header.Method method) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        Optional.ofNullable(headersMap.get(method))
                .ifPresent(map::putAll);
        return map;
    }
}
