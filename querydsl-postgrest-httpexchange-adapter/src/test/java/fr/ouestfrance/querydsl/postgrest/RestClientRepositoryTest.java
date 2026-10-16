package fr.ouestfrance.querydsl.postgrest;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@MockServerSettings(ports = 8007)
class RestClientRepositoryTest extends AbstractRepositoryTest {

    @Override
    protected HttpServiceProxyFactory proxyFactory() {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(RestClient.builder().requestFactory(new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault())).build())).build();
    }
}
