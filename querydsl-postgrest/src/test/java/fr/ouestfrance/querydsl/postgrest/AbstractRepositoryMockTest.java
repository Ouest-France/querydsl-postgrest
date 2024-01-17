package fr.ouestfrance.querydsl.postgrest;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
abstract class AbstractRepositoryMockTest {

    @SuppressWarnings("unchecked")
    ArgumentCaptor<Map<String, List<String>>> multiMapCaptor() {
        return ArgumentCaptor.forClass(Map.class);
    }
}
