package fr.ouestfrance.querydsl.postgrest;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
abstract class AbstractRepositoryMockTest {

    @SuppressWarnings("unchecked")
    ArgumentCaptor<MultiValueMap<String, Object>> multiMapCaptor() {
        return ArgumentCaptor.forClass(MultiValueMap.class);
    }
}
