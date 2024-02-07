package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostRepository;
import fr.ouestfrance.querydsl.postgrest.app.PostRequest;
import fr.ouestfrance.querydsl.postgrest.app.PostRequestWithSize;
import fr.ouestfrance.querydsl.postgrest.model.CountItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


class PostgrestRepositoryCountMockTest extends AbstractRepositoryMockTest {

    @Mock
    private PostgrestClient postgrestClient;

    private PostgrestRepository<Post> repository;

    @BeforeEach
    void beforeEach() {
        repository = new PostRepository(postgrestClient);
    }


    @Test
    void shouldCountWithoutCriteriaOrNull() {
        when(postgrestClient.count(anyString(), any())).thenReturn(List.of(CountItem.of(1)));
        assertEquals(1, repository.count(null));
        assertEquals(1, repository.count());
    }

    @Test
    void shouldCountWithCriteria() {
        PostRequest request = new PostRequest();
        request.setUserId(1);
        request.setId(1);
        request.setTitle("Test*");
        request.setCodes(List.of("a", "b", "c"));
        request.setExcludes(List.of("z"));
        request.setValidDate(LocalDate.of(2023, 11, 10));
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        when(postgrestClient.count(anyString(), queryArgs.capture())).thenReturn(List.of(CountItem.of(1)));

        assertEquals(1, repository.count(request));
        Map<String, List<String>> queries = queryArgs.getValue();
        assertEquals("eq.1", queries.get("userId").stream().findFirst().orElseThrow());
        assertEquals("neq.1", queries.get("id").stream().findFirst().orElseThrow());
        assertEquals("lte.2023-11-10", queries.get("startDate").stream().findFirst().orElseThrow());
        assertEquals("(endDate.gte.2023-11-10,endDate.is.null)", queries.get("or").stream().findFirst().orElseThrow());
        assertEquals("like.Test*", queries.get("title").stream().findFirst().orElseThrow());
        assertEquals("count()", queries.get("select").stream().findFirst().orElseThrow());
    }

    @Test
    void shouldNotUseAnotherSelect() {
        PostRequestWithSize request = new PostRequestWithSize();
        request.setSize("25");
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        when(postgrestClient.count(anyString(), queryArgs.capture())).thenReturn(List.of(CountItem.of(1)));
        assertEquals(1, repository.count(request));
        Map<String, List<String>> queries = queryArgs.getValue();
        assertEquals(1, queries.get("select").size());
        assertEquals("count()", queries.get("select").stream().findFirst().orElseThrow());
    }
}