package fr.ouestfrance.querydsl.postgrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.SimpleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class SimpleRepositoryGetTest {

    @InjectMocks
    private PostgrestRepository<Post> repository = new SimpleRepository();

    @Mock
    private PostgrestClient postgrestClient;

    @Mock
    private ObjectMapper mapper;

    @Test
    void shouldFindOne() {
        when(postgrestClient.search(anyString(), any(), any())).thenReturn(ResponseEntity.ok(List.of(new Post())));
        when(mapper.convertValue(any(), eq(Post.class))).thenReturn(new Post());
        Optional<Post> one = repository.findOne(null);
        assertNotNull(one);
        assertTrue(one.isPresent());
    }
}
