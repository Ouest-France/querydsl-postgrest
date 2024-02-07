package fr.ouestfrance.querydsl.postgrest.services;

import fr.ouestfrance.querydsl.postgrest.model.BulkOptions;
import fr.ouestfrance.querydsl.postgrest.model.BulkRequest;
import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BulkExecutorServiceTest {

    private final BulkExecutorService executorService = new BulkExecutorService();

    @Test
    void shouldBulkExecute() {
        AtomicInteger calls = new AtomicInteger(0);
        int pageSize = 50;
        int totalElements = 450;
        BulkResponse<Object> result = executorService.execute(x -> {
            calls.incrementAndGet();
            return new BulkResponse<>(List.of(), pageSize, totalElements);

        }, BulkRequest.builder().headers(new HashMap<>()).build(), BulkOptions.builder().build());
        assertEquals(totalElements, result.getTotalElements());
        assertEquals(totalElements, result.getAffectedRows());
        assertEquals(totalElements / pageSize, calls.get());
    }

    @Test
    void shouldBulkExecuteWithPageSize() {
        AtomicInteger calls = new AtomicInteger(0);
        int pageSize = 10;
        int totalElements = 450;
        BulkResponse<Object> result = executorService.execute(x -> {
            calls.incrementAndGet();
            return new BulkResponse<>(List.of(), pageSize, totalElements);

        }, BulkRequest.builder().headers(new HashMap<>()).build(), BulkOptions.builder()
                .countOnly(true)
                .pageSize(pageSize)
                .build());
        assertEquals(totalElements, result.getTotalElements());
        assertEquals(totalElements, result.getAffectedRows());
        assertEquals(totalElements / pageSize, calls.get());
    }

    @Test
    void shouldBulkExecuteOnePage() {
        AtomicInteger calls = new AtomicInteger(0);
        BulkResponse<Object> result = executorService.execute(x -> {
                    calls.incrementAndGet();
                    return new BulkResponse<>(List.of(), 100, 100);
                },
                BulkRequest.builder().headers(new HashMap<>()).build(),
                BulkOptions.builder().countOnly(true).build());
        assertEquals(100, result.getTotalElements());
        assertEquals(100, result.getAffectedRows());
        assertEquals(1, calls.get());
    }

}
