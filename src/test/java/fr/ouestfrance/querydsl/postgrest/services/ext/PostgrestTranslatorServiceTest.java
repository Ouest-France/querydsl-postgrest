package fr.ouestfrance.querydsl.postgrest.services.ext;

import fr.ouestfrance.querydsl.postgrest.app.PostRequest;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.service.ext.QueryDslProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Slf4j
class PostgrestTranslatorServiceTest {

    private final QueryDslProcessorService<Filter> queryProcessorService = new PostgrestQueryProcessorService();

    @Test
    void shouldLoad() {
        PostRequest object = new PostRequest(12, 12, "tt-editions-finistere", LocalDate.now(), List.of("quimper", "morlaix"), List.of("brest"));
        List<Filter> translate = queryProcessorService.process(object);
        assertNotNull(translate);
        assertEquals(9, translate.size());

        log.info("filters {}", translate.stream().map(x -> x.getKey() + "=" + x.getFilterString()).collect(Collectors.joining("\n")));
    }
}
