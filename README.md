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

QueryDSL-PostgRest is a [PostgRest](https://github.com/postgrest/postgrest) implementation of queryDSL library

## Getting Started

QueryDSL is a **Domain Specific Language** based on annotation processing to transform java classes to dedicated query
for a specific datasource.
You can write your own connector implementation by adding this library to your code

### Maven integration

Add the following dependency to your Maven project:

```xml

<dependency>
    <groupId>fr.ouestfrance.querydsl</groupId>
    <artifactId>querydsl-postgrest</artifactId>
    <version>${querydsl-postgrest.version}</version>
</dependency>
```

### Gradle integration

Add the following dependency to your gradle project:

```groovy
implementation 'fr.ouestfrance.querydsl:querydsl-postgrest:${querydsl-postgrest.version}'
```

### Configure Postgrest

QueryDsl postgrest provides class to simplify querying postgrest api using HttpExchange and require HttpClientAdapter.
You could use any HttpClient (RestTemplate, OkHttpClient, WebClient) by using the good adapter.

You can also specify authenticators, interceptors (retry, transform) and every configuration (timeout, default headers,
cookies, ...) you need to deploy.

**Webclient configuration example**

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.PostgrestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PostgrestConfiguration {

    @Bean
    public PostgrestClient postgrestRepository() {
        String serviceUrl = "http://localhost:9000";
        WebClient webclient = WebClientAdapter.forClient(WebClient.builder()
                .baseUrl(serviceUrl).build());

        return HttpServiceProxyFactory.builder()
                .clientAdapter(webclient).build().createClient(PostgrestClient.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

Mapping to object is based on jackson ObjectMapper that allow you to configure :

- Specific type converters
- Naming strategy
- Null strategy
- ...

### Create your first repository

#### Specify your first search criteria

You can start by writing the first search object using `@FilterField` annotations. This example allow you to search user
with :

- equals filter on id key
- like filter on name key

```java
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public static class UserSearch {
    @FilterField
    String id;
    @FilterField(operation = FilterOperation.LIKE)
    String name;
}
```

#### Create your repository

To access data, you have to create Repository for your type and put `@PostgrestConfiguration` to specify extra data

| Property      | Required | Format   | Description                                                 | Example                                                               |
|---------------|----------|----------|-------------------------------------------------------------|-----------------------------------------------------------------------|
| resource      | O        | String   | Resource name  in the postgrest api                         | "users"                                                               |
| embedded      | X        | String[] | Sub resources embedded in the main resource                 | ["phone", "address"]                                                  |
| deleteHeaders | X        | String[] | Prefers values to pass to the delete methods                | @Header(key="Prefer", value={"tx=rollback", "return=representation"}) |
| upsertHeaders | X        | String[] | Prefers values to pass to the upsert methods                | @Header(key="Prefer", value="resolution=merge-duplicates")            |
| patchHeaders  | X        | String[] | Prefers values to pass to the patch methods                 | @Header(key="Prefer", value="return=representation")                  |
| countStrategy | O        | String   | Count strategy (exact, planned, estimated) default is exact | CountType.EXACT                                                       |

```java
@PostgrestConfiguration(resource = "users")
public class UserRepository extends PostgrestRepository<User> {
}
```

### Using the repository

You can then create your functions :

- getUserById : Will return user with a specific id or raise a NotFoundException
- findUsersByName : Will return list of users which name contains part of search content

```java
import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Return a specific
     * @param id id of the user
     * @return user found
     * @throws NotFoundException if there's no users with this id
     */
    public User getById(String id) throws NotFoundException {
        UserSearch search = new UserSearch();
        search.setId(id);
        return userRepository.findOne(search).
                orElseThrow(new NotFoundException(User.class, id));
    }

    /**
     * Return all users matching part of the name
     * @param name name to search
     * @return list of users
     */
    public List<User> findByName(String name) {
        UserSearch search = new UserSearch();
        search.setName(name);
        return userRepository.search(search);
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

[coverage-image]: https://codecov.io/gh/ouest-france/querydsl-postgrest/graph/badge.svg?token=OSDY72YC4E

[coverage-url]: https://codecov.io/gh/ouest-france/querydsl-postgrest

[maven-central-image]: https://maven-badges.herokuapp.com/maven-central/fr.ouestfrance.querydsl/querydsl-postgrest/badge.svg

[maven-central-url]: http://search.maven.org/#search%7Cga%7C1%7Cfr.ouestfrance.querydsl

[sonar-image]: https://sonarcloud.io/api/project_badges/measure?project=Ouest-France_querydsl-postgrest&metric=alert_status

[sonar-url]: https://sonarcloud.io/summary/new_code?id=Ouest-France_querydsl-postgrest