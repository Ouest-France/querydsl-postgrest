<div align="center" style="text-align:center;padding-top: 15px">
    <img alt="logo-ouest-france" src="https://sipaui.sipaof.fr/downloads/logotheque/ouest-france-couleur.svg" height="100"/>
    <h1 style="margin: 0;padding: 0">QueryDSL-PostgRest</h1>
</div>
<div align="center" style="text-align: center">

[![Build Status][maven-build-image]][maven-build-url]
[![Coverage][coverage-image]][coverage-url]
[![Quality Gate Status][sonar-image]][sonar-url]
[![Download][maven-central-image]][maven-central-url]

</div>

**QueryDSL-PostgRest-WebClient-Adapter** is a httpclient adapter of [QueryDSL](https://github.com/Ouest-France/querydsl).

**PostgREST** is an open source project that provides a fully RESTful API from any existing PostgreSQL database

## Getting Started

### Maven integration

Add the following dependency to your Maven project:

```xml
<dependency>
    <groupId>fr.ouestfrance.querydsl</groupId>
    <artifactId>querydsl-postgrest-webclient-adapter</artifactId>
    <version>${querydsl-postgrest.version}</version>
</dependency>
```

### Gradle integration

Add the following dependency to your gradle project:

```groovy
implementation 'fr.ouestfrance.querydsl:querydsl-postgrest-webclient-adapter:${querydsl-postgrest.version}'
```

### Configure PostgrestClient
QueryDsl postgrest need implementation of PostgrestClient with a specific HttpAdapter.

It's really easy to create your own HttpClientAdapter (RestTemplate, OkHttpClient, HttpConnexion, ...) by
implementing `PostgrestClient` interface.

You can also specify authenticators, interceptors (retry, transform) and every configuration (timeout, default headers,
cookies, ...) you need to deploy.

#### Configuration example

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.PostgrestClient;
import fr.ouestfrance.querydsl.postgrest.PostgrestWebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PostgrestConfiguration {

    @Bean
    public PostgrestClient podstgrestClient() {
        String serviceUrl = "http://localhost:9000";
        WebClient webclient = WebClient.builder()
                .baseUrl(serviceUrl)
                // Here you can add any filters or default configuration you want
                .build();

        return PostgrestWebClient.of(webclient);
    }
}
```
## Need Help ?

If you need help with the library please start a new thread QA / Issue on github

## Contributing

If you want to request a feature or report a bug, please create a GitHub Issue

If you want to make a contribution to the project, please create a PR

## License

The QueryDSL is licensed under [MIT License](https://opensource.org/license/mit/)

[maven-build-image]: https://github.com/Ouest-France/querydsl-postgrest/actions/workflows/build.yml/badge.svg

[maven-build-url]: https://github.com/Ouest-France/querydsl-postgrest/actions/workflows/build.yml

[coverage-image]: https://codecov.io/gh/ouest-france/querydsl-postgrest/graph/badge.svg

[coverage-url]: https://codecov.io/gh/ouest-france/querydsl-postgrest

[maven-central-image]: https://maven-badges.herokuapp.com/maven-central/fr.ouestfrance.querydsl/querydsl-postgrest/badge.svg

[maven-central-url]: https://mvnrepository.com/artifact/fr.ouestfrance.querydsl/querydsl-postgrest

[sonar-image]: https://sonarcloud.io/api/project_badges/measure?project=Ouest-France_querydsl-postgrest&metric=alert_status

[sonar-url]: https://sonarcloud.io/summary/new_code?id=Ouest-France_querydsl-postgrest
