package fr.ouestfrance.querydsl.postgrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.model.*;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.MissingConfigurationException;
import fr.ouestfrance.querydsl.postgrest.model.impl.OrderFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SelectFilter;
import fr.ouestfrance.querydsl.postgrest.services.ext.PostgrestQueryProcessorService;
import fr.ouestfrance.querydsl.service.ext.QueryDslProcessorService;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Postgrest repository implementation
 *
 * @param <T> type of returned entity
 */
@Slf4j
public abstract class PostgrestRepository<T> implements Repository<T> {

    private final QueryDslProcessorService<Filter> processorService = new PostgrestQueryProcessorService();
    private final PostgrestConfiguration annotation;
    private final Class<T> clazz;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PostgrestClient webClient;


    /**
     * Constructor
     */
    protected PostgrestRepository() {
        if (!getClass().isAnnotationPresent(PostgrestConfiguration.class)) {
            throw new MissingConfigurationException(getClass(),
                    "Missing annotation " + PostgrestConfiguration.class.getSimpleName());
        }
        annotation = getClass().getAnnotation(PostgrestConfiguration.class);
        //noinspection unchecked
        clazz = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), PostgrestRepository.class);
    }

    @Override
    public Page<T> search(Object criteria, @Nonnull Pageable pageable) {
        List<Filter> queryParams = processorService.process(criteria);
        Map<String, Object> headers = new HashMap<>();
        // Add pageable if present
        if (pageable.getPageSize() > 0) {
            headers.put(Headers.RANGE_UNIT, "items");
            headers.put(Headers.RANGE, pageable.toRange());
            headers.put(Headers.PREFER, "count=exact");
        }
        // Add sort if present
        if (pageable.getSort() != null) {
            queryParams.add(OrderFilter.of(pageable.getSort()));
        }
        // Add select criteria
        if (annotation.embedded().length > 0) {
            queryParams.add(SelectFilter.of(annotation.embedded()));
        }
        ResponseEntity<List<Object>> response = webClient.search(annotation.resource(), toMap(queryParams), headers);
        // Retrieve result headers
        return Optional.ofNullable(response.getBody()).map(x -> {
            PageImpl<Object> page = new PageImpl<>(x, pageable, x.size(), 1);
            List<String> contentRangeHeaders = response.getHeaders().get(Headers.CONTENT_RANGE);
            if (contentRangeHeaders != null && !contentRangeHeaders.isEmpty()) {
                Range range = Range.of(contentRangeHeaders.stream().findFirst().toString());
                page.withRange(range);
            }
            return page.map(this::toEntity);
        }).orElse(Page.of());
    }

    @Override
    public List<T> upsert(List<Object> values) {
        MultiValueMap<String, Object> headers = new LinkedMultiValueMap<>();
        headers.add(Headers.PREFER, annotation.upsertHeaders());
        return webClient.post(annotation.resource(), values, headers).stream()
                .map(this::toEntity).toList();
    }


    @Override
    public List<T> patch(Object criteria, Object body) {
        MultiValueMap<String, Object> headers = new LinkedMultiValueMap<>();
        headers.add(Headers.PREFER, annotation.upsertHeaders());
        List<Filter> queryParams = processorService.process(criteria);
        return webClient.patch(resourceName(), toMap(queryParams), body, headers)
                .stream().map(this::toEntity)
                .toList();
    }


    @Override
    public List<T> delete(Object criteria) {
        MultiValueMap<String, Object> headers = new LinkedMultiValueMap<>();
        headers.add(Headers.PREFER, annotation.deleteHeaders());
        List<Filter> queryParams = processorService.process(criteria);
        return webClient.delete(annotation.resource(), toMap(queryParams), headers).stream()
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
     * Convert result object to entity
     *
     * @param value object value
     * @return entity object
     */
    private T toEntity(Object value) {
        return objectMapper.convertValue(value, clazz);
    }

}
