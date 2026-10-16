<div align="center" style="text-align:center;padding-top: 15px">
    <img alt="logo-ouest-france" src="https://sipaui.sipaof.fr/downloads/logotheque/ouest-france-couleur.svg" height="100"/>
    <h1 style="margin: 0;padding: 0">PostgRest-HttpExchange-Adapter</h1>
</div>
<div align="center" style="text-align: center">

[![Build Status][maven-build-image]][maven-build-url]
[![Coverage][coverage-image]][coverage-url]
[![Quality Gate Status][sonar-image]][sonar-url]
[![Download][maven-central-image]][maven-central-url]

</div>

**QueryDSL-PostgRest-HttpExchange-Adapter** is a httpclient adapter of [QueryDSL-Postgrest](https://github.com/Ouest-France/querydsl-postgrest).

**PostgREST** is an open source project that provides a fully RESTful API from any existing PostgreSQL database

## Getting Started

### Maven integration

Add the following dependency to your Maven project:

```xml
<dependency>
    <groupId>fr.ouestfrance.querydsl</groupId>
    <artifactId>querydsl-postgrest-httpexchange-adapter</artifactId>
    <version>${querydsl-postgrest.version}</version>
</dependency>
```

### Gradle integration

Add the following dependency to your gradle project:

```groovy
implementation 'fr.ouestfrance.querydsl:querydsl-postgrest-httpexchange-adapter:${querydsl-postgrest.version}'
```

### Configure PostgrestClient
This adapter relies on Spring `@HttpExchange` : you create the `HttpServiceProxyFactory` yourself from the HTTP client
you want (`RestClient` or `WebClient`), so all the client configuration (timeouts, authentication, interceptors, ...)
stays in your hands. Responses are read as raw bytes and converted to json with Jackson.

`spring-aop` and `spring-context` are required by `HttpServiceProxyFactory`; `spring-webflux` only if you use a `WebClient`.

#### With a RestClient

```java
@Bean
public PostgrestClient postgrestClient() {
    RestClient restClient = RestClient.builder().baseUrl("http://localhost:9000").build();
    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
    return PostgrestHttpExchange.of(factory, "http://localhost:9000");
}
```

#### With a WebClient

```java
@Bean
public PostgrestClient postgrestClient() {
    WebClient webClient = WebClient.builder().build();
    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build();
    return PostgrestHttpExchange.of(factory, "http://localhost:9000");
}
```

A custom `tools.jackson.databind.json.JsonMapper` can be given to read responses :
`PostgrestHttpExchange.of(factory, url, jsonMapper)`.

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
