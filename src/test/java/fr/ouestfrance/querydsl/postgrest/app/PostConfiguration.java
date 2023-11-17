package fr.ouestfrance.querydsl.postgrest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.PostgrestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.UUID;

@Configuration
@Slf4j
public class PostConfiguration {


    @Bean
    public HttpServiceProxyFactory postFactory() {
        return HttpServiceProxyFactory.builder()
                .clientAdapter(WebClientAdapter.forClient(WebClient.builder()
                        .filter((request, next) -> {
                            ClientRequest newRequest = ClientRequest.from(request)
                                    .header("Authorization", "Bearer test")
                                    .header("X-Correlation-ID", UUID.randomUUID().toString())
                                    .build();
                            log.info("request called {}", newRequest.url());
                            return next.exchange(newRequest);
                        })
                        .baseUrl("https://jsonplaceholder.typicode.com")
                        .build()))
                .build();
    }

    @Bean
    public PostgrestClient postgrestRepository(HttpServiceProxyFactory factory){
        return factory.createClient(PostgrestClient.class);
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
