package fr.ouestfrance.querydsl.postgrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ALL")
@ExtendWith(MockitoExtension.class)
@Slf4j
class PostgrestRepositoryUpsertTest {

    @InjectMocks
    private PostgrestRepository<Post> repository = new PostRepository();

    @Mock
    private PostgrestClient postgrestClient;

    @Mock
    private ObjectMapper mapper;

    private ObjectMapper realMapper = new ObjectMapper();


    @Test
    void shouldUpsert() {
        ArgumentCaptor<List<Object>> postCaptor = ArgumentCaptor.forClass(List.class);
        String generateId = UUID.randomUUID().toString();

        Post save = new Post();
        save.setTitle("title");
        save.setBody("test");

        when(postgrestClient.post(anyString(), postCaptor.capture(), any())).thenAnswer(x->{
            Post post = new Post();
            post.setId(generateId);
            post.setTitle(save.getTitle());
            post.setBody(save.getBody());
            return List.of(post);
        });
        when(mapper.convertValue(any(), eq(Post.class))).thenAnswer(x->realMapper.convertValue(x.getArguments()[0], Post.class));
        Post saved =  repository.upsert(save);
        assertNotNull(saved);

        assertEquals(1, postCaptor.getValue().size());
        assertEquals(generateId, saved.getId());
        assertEquals(save.getBody(), saved.getBody());
        assertEquals(save.getTitle(), saved.getTitle());
    }
}
