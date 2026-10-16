package fr.ouestfrance.querydsl.postgrest;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@MockServerSettings(ports = 8007)
class WebClientRepositoryTest extends AbstractRepositoryTest {

    @Override
    protected HttpServiceProxyFactory proxyFactory() {
        return HttpServiceProxyFactory.builderFor(WebClientAdapter.create(WebClient.builder().build())).build();
    }
}
