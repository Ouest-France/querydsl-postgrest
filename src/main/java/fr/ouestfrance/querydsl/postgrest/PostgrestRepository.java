package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.annotations.Header;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.PageImpl;
import fr.ouestfrance.querydsl.postgrest.model.Pageable;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.MissingConfigurationException;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import fr.ouestfrance.querydsl.postgrest.model.impl.OrderFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SelectFilter;
import fr.ouestfrance.querydsl.postgrest.services.ext.PostgrestQueryProcessorService;
import fr.ouestfrance.querydsl.service.ext.QueryDslProcessorService;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

/**
 * Postgrest repository implementation
 *
 * @param <T> type of returned entity
 */
public class PostgrestRepository<T> implements Repository<T> {

    private final QueryDslProcessorService<Filter> processorService = new PostgrestQueryProcessorService();
    private final PostgrestConfiguration annotation;
    private final Map<Header.Method, MultiValueMap<String, String>> headersMap = new EnumMap<>(Header.Method.class);
    private final PostgrestClient client;

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
        Arrays.stream(getClass().getAnnotationsByType(Header.class)).forEach(header -> Arrays.stream(header.methods())
                .forEach(method ->
                        headersMap.computeIfAbsent(method, x -> new LinkedMultiValueMap<>())
                                .addAll(header.key(), Arrays.asList(header.value()))
                )
        );
    }

    @Override
    public Page<T> search(Object criteria, Pageable pageable) {
        List<Filter> queryParams = processorService.process(criteria);
        MultiValueMap<String, String> headers = headerMap(Header.Method.GET);
        // Add pageable if present
        if (pageable.getPageSize() > 0) {
            headers.add("Range-Unit", "items");
            headers.add("Range", pageable.toRange());
            headers.add("Prefers", "count=" + annotation.countStrategy().name().toLowerCase());
        }
        // Add sort if present
        if (pageable.getSort() != null) {
            queryParams.add(OrderFilter.of(pageable.getSort()));
        }
        // Add select criteria
        getSelects(criteria).ifPresent(queryParams::add);
        Page<T> response = client.search(annotation.resource(), toMap(queryParams), headers);
        if (response instanceof PageImpl<T> page) {
            page.setPageable(pageable);
        }
        // Retrieve result headers
        return response;
    }

    @Override
    public List<T> upsert(List<Object> values) {
        return client.post(annotation.resource(), values, headerMap(Header.Method.UPSERT));
    }


    @Override
    public List<T> patch(Object criteria, Object body) {
        List<Filter> queryParams = processorService.process(criteria);
        // Add select criteria
        getSelects(criteria).ifPresent(queryParams::add);
        return client.patch(annotation.resource(), toMap(queryParams), body, headerMap(Header.Method.UPSERT));
    }


    @Override
    public List<T> delete(Object criteria) {
        List<Filter> queryParams = processorService.process(criteria);
        // Add select criteria
        getSelects(criteria).ifPresent(queryParams::add);
        return client.delete(annotation.resource(), toMap(queryParams), headerMap(Header.Method.DELETE));
    }

    /**
     * Transform a filter list to map of queryString
     *
     * @param filters list of filters
     * @return map of query strings
     */
    private MultiValueMap<String, String> toMap(List<Filter> filters) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        filters.forEach(x -> map.add(x.getKey(), x.getFilterString()));
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
        if (attributes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(SelectFilter.of(attributes));
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
    private MultiValueMap<String, String> headerMap(Header.Method method) {
        return Optional.ofNullable(headersMap.get(method)).orElse(new LinkedMultiValueMap<>());
    }
}
