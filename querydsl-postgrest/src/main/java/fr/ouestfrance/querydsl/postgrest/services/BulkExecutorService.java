package fr.ouestfrance.querydsl.postgrest.services;

import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import fr.ouestfrance.querydsl.postgrest.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BulkExecutorService {

    public <T> BulkResponse<T> execute(Function<BulkRequest, BulkResponse<T>> function, BulkRequest request, BulkOptions options) {
        List<String> prefers = request.getHeaders().computeIfAbsent(Prefer.HEADER, x -> new ArrayList<>());
        if (options.isCountOnly()) {
            prefers.stream().filter(x -> x.startsWith("return")).findFirst().ifPresent(prefers::remove);
            prefers.add(Prefer.Return.HEADERS_ONLY);
        }
        // Add default count
        prefers.add("count=" + PostgrestConfiguration.CountType.EXACT.name().toLowerCase());
        // If we want to bulk update (multiple page)
        Pageable pageable = null;
        if (options.getPageSize() > 0) {
            pageable = Pageable.ofSize(options.getPageSize());
            request.getHeaders().put("Range-Unit", List.of("items"));
        }
        // Do first call
        BulkResponse<T> response = new BulkResponse<>(null, 0, 0);
        response.merge(function.apply(request));
        // If pageable is null -> Retrieve default pageable from the first response
        if (pageable == null) {
            pageable = Pageable.ofSize((int) response.getAffectedRows());
        }

        // If everything is not found then do next calls
        while (hasNextPage(response)) {
            // Start on page 1
            pageable = pageable.next();
            request.getHeaders().put("Range", List.of(pageable.toRange()));
            response.merge(function.apply(request));
        }
        return response;
    }

    private static <T> boolean hasNextPage(BulkResponse<T> response) {
        return response.getTotalElements() > response.getAffectedRows();
    }
}
