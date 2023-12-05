package fr.ouestfrance.querydsl.postgrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.annotations.Header;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;
import fr.ouestfrance.querydsl.postgrest.model.*;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.MissingConfigurationException;
import fr.ouestfrance.querydsl.postgrest.model.impl.OrderFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SelectFilter;
import fr.ouestfrance.querydsl.postgrest.services.ext.PostgrestQueryProcessorService;
import fr.ouestfrance.querydsl.service.ext.QueryDslProcessorService;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

/**
 * Postgrest repository implementation
 *
 * @param <T> type of returned entity
 */
public abstract class PostgrestRepository<T> implements Repository<T> {

    private final QueryDslProcessorService<Filter> processorService = new PostgrestQueryProcessorService();
    private final PostgrestConfiguration annotation;
    private final Class<T> clazz;
    private final Map<Header.Method, MultiValueMap<String, Object>> headersMap = new EnumMap<>(Header.Method.class);
    private final ObjectMapper objectMapper;
    private final PostgrestClient webClient;

    protected PostgrestRepository(PostgrestClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
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
        //noinspection unchecked
        clazz = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), PostgrestRepository.class);
    }

    @Override
    public Page<T> search(Object criteria, Pageable pageable) {
        List<Filter> queryParams = processorService.process(criteria);
        MultiValueMap<String, Object> headers = headersMap.get(Header.Method.GET);
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
        ResponseEntity<List<Object>> response = webClient.search(annotation.resource(), toMap(queryParams), headers);
        // Retrieve result headers
        return Optional.ofNullable(response.getBody()).map(x -> {
            PageImpl<Object> page = new PageImpl<>(x, pageable, x.size(), 1);
            List<String> contentRangeHeaders = response.getHeaders().get("Content-Range");
            if (contentRangeHeaders != null && !contentRangeHeaders.isEmpty()) {
                Range range = Range.of(contentRangeHeaders.stream().findFirst().toString());
                page.withRange(range);
            }
            return page.map(this::toEntity);
        }).orElse(Page.of());
    }

    @Override
    public List<T> upsert(List<Object> values) {
        return webClient.post(annotation.resource(), values, headersMap.get(Header.Method.UPSERT)).stream()
                .map(this::toEntity).toList();
    }


    @Override
    public List<T> patch(Object criteria, Object body) {
        List<Filter> queryParams = processorService.process(criteria);
        // Add select criteria
        getSelects(criteria).ifPresent(queryParams::add);
        return webClient.patch(resourceName(), toMap(queryParams), body, headersMap.get(Header.Method.PATCH))
                .stream().map(this::toEntity)
                .toList();
    }


    @Override
    public List<T> delete(Object criteria) {
        List<Filter> queryParams = processorService.process(criteria);
        // Add select criteria
        getSelects(criteria).ifPresent(queryParams::add);
        return webClient.delete(annotation.resource(), toMap(queryParams), headersMap.get(Header.Method.DELETE)).stream()
                .map(this::toEntity).toList();
    }


    @Override
    public String resourceName() {
        return annotation.resource();
    }

    /**
     * Transform a filter list to map of queryString
     *
     * @param filters list of filters
     * @return map of query strings
     */
    private MultiValueMap<String, Object> toMap(List<Filter> filters) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
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
     * Convert result object to entity
     *
     * @param value object value
     * @return entity object
     */
    private T toEntity(Object value) {
        return objectMapper.convertValue(value, clazz);
    }

}
