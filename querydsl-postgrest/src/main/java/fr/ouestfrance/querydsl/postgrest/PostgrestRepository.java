package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.annotations.Header;
import fr.ouestfrance.querydsl.postgrest.annotations.OnConflict;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import fr.ouestfrance.querydsl.postgrest.model.*;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.MissingConfigurationException;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import fr.ouestfrance.querydsl.postgrest.model.impl.CountFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.OrderFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SelectFilter;
import fr.ouestfrance.querydsl.postgrest.services.BulkExecutorService;
import fr.ouestfrance.querydsl.postgrest.services.ext.PostgrestQueryProcessorService;
import fr.ouestfrance.querydsl.postgrest.utils.FilterUtils;
import fr.ouestfrance.querydsl.service.ext.QueryDslProcessorService;

import java.lang.reflect.ParameterizedType;
import java.util.*;

import static fr.ouestfrance.querydsl.postgrest.annotations.Header.Method.*;
import static fr.ouestfrance.querydsl.postgrest.utils.FilterUtils.toMap;

/**
 * Postgrest repository implementation
 *
 * @param <T> type of returned entity
 */
public class PostgrestRepository<T> implements Repository<T> {

    public static final String ON_CONFLICT_QUERY_PARAMS = "on_conflict";
    private final QueryDslProcessorService<Filter> processorService = new PostgrestQueryProcessorService();
    private final BulkExecutorService bulkService = new BulkExecutorService();
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
        }
        headers.computeIfAbsent(Prefer.HEADER, x -> new ArrayList<>())
                .add("count=" + annotation.countStrategy().name().toLowerCase());
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
    public long count(Object criteria) {
        List<Filter> queryParams = processorService.process(criteria);
        queryParams.add(CountFilter.of());
        List<CountItem> response = client.count(annotation.resource(), toMap(queryParams));
        // Retrieve result headers
        return response.stream().findFirst().map(x -> x.get("count")).map(Long::valueOf).orElse(0L);
    }


    @Override
    public BulkResponse<T> post(List<Object> values) {
        return client.post(annotation.resource(), new HashMap<>(), values, headerMap(UPSERT), clazz);
    }

    @Override
    public BulkResponse<T> post(List<Object> values, BulkOptions options) {
        // Add return representation headers only
        return bulkService.execute(x -> client.post(annotation.resource(), new HashMap<>(), values, x.getHeaders(), clazz),
                BulkRequest.builder().headers(headerMap(UPSERT)).build(),
                options);
    }

    @Override
    public BulkResponse<T> upsert(List<Object> values) {
        return client.post(annotation.resource(), getUpsertQueryParams(), values, getUpsertHeaderMap(), clazz);
    }

    @Override
    public BulkResponse<T> upsert(List<Object> values, BulkOptions options) {
        // Add return representation headers only
        return bulkService.execute(x -> client.post(annotation.resource(), getUpsertQueryParams(), values, x.getHeaders(), clazz),
                BulkRequest.builder().headers(getUpsertHeaderMap()).build(),
                options);
    }

    /**
     * Retrieve on conflict query params
     * @return map of query params for on conflict if annotation OnConflict is present otherwise empty map
     */
    private Map<String, List<String>> getUpsertQueryParams() {
        OnConflict onConflict = this.getClass().getAnnotation(OnConflict.class);
        if (Objects.nonNull(onConflict)) {
            return Map.of(ON_CONFLICT_QUERY_PARAMS, List.of(String.join(",", onConflict.columnNames())));
        }
        return Collections.emptyMap();
    }

    /**
     * Retrieve header map for upsert
     * @return map of headers for upsert
     */
    private Map<String, List<String>> getUpsertHeaderMap() {
        Map<String, List<String>> headerMap = headerMap(UPSERT);
        headerMap.computeIfAbsent(Prefer.HEADER, x -> new ArrayList<>())
                .add(Prefer.Resolution.MERGE_DUPLICATES);
        return headerMap;
    }

    @Override
    public BulkResponse<T> patch(Object criteria, Object body, BulkOptions options) {
        List<Filter> filters = processorService.process(criteria);
        getSelects(criteria).ifPresent(filters::add);
        return bulkService.execute(x -> client.patch(annotation.resource(), toMap(filters), body, x.getHeaders(), clazz),
                BulkRequest.builder().headers(headerMap(PATCH)).build(),
                options);
    }


    @Override
    public BulkResponse<T> delete(Object criteria, BulkOptions options) {
        List<Filter> filters = processorService.process(criteria);
        getSelects(criteria).ifPresent(filters::add);
        return bulkService.execute(x -> client.delete(annotation.resource(), toMap(filters), x.getHeaders(), clazz),
                BulkRequest.builder().headers(headerMap(DELETE)).build(),
                options);
    }

    /**
     * Extract selection on criteria and class
     *
     * @param criteria search criteria
     * @return attributes
     */
    private Optional<Filter> getSelects(Object criteria) {
        return Optional.of(FilterUtils.getSelectAttributes(this, criteria))
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
        List<Filter> queryParams = processorService.process(criteria);
        Map<String, List<String>> headers = headerMap(Header.Method.GET);

        // Add select criteria
        getSelects(criteria).ifPresent(queryParams::add);
        RangeResponse<T> search = client.search(annotation.resource(), toMap(queryParams), headers, clazz);

        if (search.getTotalElements() > 1) {
            throw new PostgrestRequestException(annotation.resource(),
                    "Search with params " + criteria + " must found only one result, but found " + search.getTotalElements() + " results");
        }
        return search.data().stream().findFirst();
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
                .ifPresent(headerMap -> headerMap.forEach((key, value) -> map.computeIfAbsent(key, x -> new ArrayList<>()).addAll(value)));
        return map;
    }
}
