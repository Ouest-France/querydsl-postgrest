package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostRepository;
import fr.ouestfrance.querydsl.postgrest.app.PostRequest;
import fr.ouestfrance.querydsl.postgrest.app.PostRequestWithSize;
import fr.ouestfrance.querydsl.postgrest.model.RangeResponse;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static fr.ouestfrance.querydsl.postgrest.model.impl.SelectFilter.POSTGREST_SELECT_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PostgrestRepositoryCountMockTest extends AbstractRepositoryMockTest {

    private final PostgrestClient postgrestClient = mock(PostgrestClient.class);

    private final PostgrestRepository<Post> repository = new PostRepository(postgrestClient);

    @AfterEach
    void afterEach() {
        reset(postgrestClient);
    }

    @Test
    void shouldCountWhithoutCriteriaOrNull() {
        when(postgrestClient.search(anyString(), any(), any(), eq(Map.class))).thenReturn(RangeResponse.of(Map.of("count", 1)));
        assertEquals(1, repository.count(null));
        assertEquals(1, repository.count());
    }

    @Test
    void shouldCountWhithCriteria() {
        PostRequest request = new PostRequest();
        request.setUserId(1);
        request.setId(1);
        request.setTitle("Test*");
        request.setCodes(List.of("a", "b", "c"));
        request.setExcludes(List.of("z"));
        request.setValidDate(LocalDate.of(2023, 11, 10));
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        ArgumentCaptor<Map<String, List<String>>> headerArgs = multiMapCaptor();
        when(postgrestClient.search(anyString(), queryArgs.capture(), headerArgs.capture(), eq(Map.class))).thenReturn(RangeResponse.of(Map.of("count", 1)));

        assertEquals(1, repository.count(request));
        Map<String, List<String>> queries = queryArgs.getValue();
        assertEquals("eq.1", queries.get("userId").stream().findFirst().orElseThrow());
        assertEquals("neq.1", queries.get("id").stream().findFirst().orElseThrow());
        assertEquals("lte.2023-11-10", queries.get("startDate").stream().findFirst().orElseThrow());
        assertEquals("(endDate.gte.2023-11-10,endDate.is.null)", queries.get("or").stream().findFirst().orElseThrow());
        assertEquals("like.Test*", queries.get("title").stream().findFirst().orElseThrow());
        assertEquals(POSTGREST_SELECT_COUNT, queries.get("select").stream().findFirst().orElseThrow());
    }

    @Test
    void shouldRaiseExceptionOnMultipleOne() {
        when(postgrestClient.search(anyString(), any(), any(), eq(Map.class))).thenReturn(RangeResponse.of(Map.of()));
        assertThrows(PostgrestRequestException.class, repository::count);
    }

    @Test
    void shouldNotUseAnotherSelect() {
        PostRequestWithSize request = new PostRequestWithSize();
        request.setSize("25");
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        ArgumentCaptor<Map<String, List<String>>> headerArgs = multiMapCaptor();
        when(postgrestClient.search(anyString(), queryArgs.capture(), headerArgs.capture(), eq(Map.class))).thenReturn(RangeResponse.of(Map.of("count", 1)));
        assertEquals(1, repository.count(request));
        Map<String, List<String>> queries = queryArgs.getValue();
        assertEquals(POSTGREST_SELECT_COUNT, queries.get("select").stream().findFirst().orElseThrow());
        assertEquals(1, (long) queries.get("select").size());
    }
}
