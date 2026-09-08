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

**QueryDSL-PostgRest** is a [PostgRest](https://github.com/postgrest/postgrest) implementation
of [QueryDSL](https://github.com/Ouest-France/querydsl) library
and provides class and annotation to improve your developer experience using PostgRest.

**PostgREST** is an open source project that provides a fully RESTful API from any existing PostgreSQL database

## Getting Started

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
implementation "fr.ouestfrance.querydsl:querydsl-postgrest:$querydslPostgrestVersion"
```

### Configure Postgrest

QueryDsl postgrest provides class to simplify querying postgrest api using PostgrestClient,
It ships two ready to use adapters, `PostgrestWebClient` and `PostgrestRestTemplate`, each in its own
artifact.

It's really easy to create your own HttpClientAdapter (RestTemplate, OkHttpClient, HttpConnexion, ...) by
implementing `PostgrestClient` interface.

You can also specify authenticators, interceptors (retry, transform) and every configuration (timeout, default headers,
cookies, ...) you need to deploy.

#### WebClient configuration example

Add the dependency :

```xml

<dependency>
    <groupId>fr.ouestfrance.querydsl</groupId>
    <artifactId>querydsl-postgrest-webclient-adapter</artifactId>
    <version>${querydsl-postgrest.version}</version>
</dependency>
```

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
    public PostgrestClient postgrestClient() {
        String serviceUrl = "http://localhost:9000";
        WebClient webclient = WebClient.builder()
                .baseUrl(serviceUrl)
                // Here you can add any filters or default configuration you want
                .build();

        return PostgrestWebClient.of(webclient);
    }
}
```

#### RestTemplate configuration example

Add the dependency :

```xml

<dependency>
    <groupId>fr.ouestfrance.querydsl</groupId>
    <artifactId>querydsl-postgrest-resttemplate-adapter</artifactId>
    <version>${querydsl-postgrest.version}</version>
</dependency>
```

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.PostgrestClient;
import fr.ouestfrance.querydsl.postgrest.PostgrestRestTemplate;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PostgrestConfiguration {

    @Bean
    public PostgrestClient postgrestClient() {
        String serviceUrl = "http://localhost:9000";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));
        return PostgrestRestTemplate.of(restTemplate, serviceUrl);
    }
}
```

### Create your first repository

#### Specify your first search criteria

You can start by writing the first search object using `@FilterField` annotations. This example allow you to search user
with :

- equals filter on id key
- like filter on name key

```java
import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterOperation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearch {
    @FilterField
    String id;
    @FilterField(operation = FilterOperation.LIKE.class)
    String name;
}
```

*@Since 1.1.0 - Record Support*

```java
import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterOperation;

public record UserSearch(
        @FilterField String id,
        @FilterField(operation = FilterOperation.LIKE.class) String name
) {
}
```

#### Create your repository

To access data, you have to create Repository for your type and put `@PostgrestConfiguration` to specify extra data

| Property      | Required | Format      | Description                                                 | Example         |
|---------------|----------|-------------|-------------------------------------------------------------|-----------------|
| resource      | O        | `String`    | Resource name in the postgrest api                          | "users"         |
| countStrategy | X        | `CountType` | Count strategy (exact, planned, estimated) default is exact | CountType.EXACT |

```java
import fr.ouestfrance.querydsl.postgrest.PostgrestClient;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import org.springframework.stereotype.Repository;

@Repository
@PostgrestConfiguration(resource = "users")
public class UserRepository extends PostgrestRepository<User> {

    public UserRepository(PostgrestClient client) {
        super(client);
    }

}
```

##### PostgrestRepository functions

| Method         | Return             | Parameters                                                                      | Description                                                                                                                                                       |
|----------------|--------------------|---------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| search         | `Page<T>`          | criteria : `Object`<br/>pageRequest : `Pageable` (optional)                     | Search request based on criteria and pagination                                                                                                                   |
| searchAsStream | `Stream<T>`        | criteria : `Object`<br/>pageSize : `int` and sort : `Sort` (optional)           | Stream all matching entities, fetching them page by page                                                                                                          |
| count          | `long`             | criteria : `Object` (optional)                                                  | Count the entities matching the criteria, all of them when no criteria is given                                                                                   |
| findOne        | `Optional<T>`      | criteria : `Object`                                                             | find one entity based on criteria<br/>Raise `PostgrestRequestException` if criteria return more than one item                                                      |
| getOne         | `T`                | criteria : `Object`                                                             | get one entity based on criteria<br/>Raise `PostgrestRequestException` if criteria returned no entity                                                              |
| post           | `T`                | value : `Object`                                                                | post data                                                                                                                                                         |
| post           | `BulkResponse<T>`  | values : `List<Object>`<br/>options : `BulkOptions` (optional)                  | post list of datas                                                                                                                                                |
| upsert         | `T`                | value : `Object`                                                                | Insert or Update data. You can specify which properties form the unique constraint by adding @OnConflict annotation on your implementation of PostgrestRepository  |
| upsert         | `BulkResponse<T>`  | values : `List<Object>`<br/>options : `BulkOptions` (optional)                  | Insert or Update datas. You can specify which properties form the unique constraint by adding @OnConflict annotation on your implementation of PostgrestRepository |
| patch          | `BulkResponse<T>`  | criteria : `Object`<br/>body : `Object`<br/>options : `BulkOptions` (optional)  | Update entities found by criterias                                                                                                                                |
| delete         | `BulkResponse<T>`  | criteria : `Object`<br/>options : `BulkOptions` (optional)                      | Delete entities found by the criteria                                                                                                                             |

`BulkResponse<T>` extends `ArrayList<T>` and carries the affected rows count of the operation.

### Using the repository

You can then create your functions :

- getUserById : Will return user with a specific id or raise a NotFoundException
- findUsersByName : Will return list of users which name contains part of search content

```java
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
     */
    public User getById(String id) {
        UserSearch criteria = new UserSearch();
        criteria.setId(id);
        return userRepository.getOne(criteria);
    }

    /**
     * Return all users matching part of the name
     * @param name name to search
     * @return list of users
     */
    public List<User> findByName(String name) {
        UserSearch search = new UserSearch();
        search.setName(name);
        // search returns a Page<User>, which is an Iterable exposing its content through getData()
        return userRepository.search(search).getData();
    }
}
```

### Advanced features

#### Vertical filtering

When certain columns are wide (such as those holding binary data), it is more efficient for the server to withhold them
in a response. The client can specify which columns are required using the select parameter.
This can be defined by annotation `@Select`

Select annotation can be added on the Repository but also to the criteria object that allow you to add specific
selection for filtering

| Property | Required | Format     | Description                                        | Example     |
|----------|----------|------------|----------------------------------------------------|-------------|
| value    | O        | `String[]` | select values to add                               | "firstname" |
| alias    | X        | `String`   | renaming column or alias                           | "fullName"  |
| only     | X        | `boolean`  | select only the specified fields, default is false | true        |

You can add extra selection by adding `@Select` annotation.
In this example there is an inner join on `Posts.author` and selecting only `firstName` and `lastName`

```java
import fr.ouestfrance.querydsl.postgrest.PostgrestClient;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;

@PostgrestConfiguration(resource = "posts")
@Select(alias = "author", value = "author!inner(firstName, lastName)")
public class PostRepository extends PostgrestRepository<Post> {

    public PostRepository(PostgrestClient client) {
        super(client);
    }

}
```

Will return json like this :

```json
[
  {
    "id": 1,
    "title": "Post 1",
    "author": {
      "firstName": "John",
      "lastName": "Doe"
    }
  },
  {
    "id": 2,
    "title": "Post 2",
    "author": {
      "firstName": "Jane",
      "lastName": "Doe"
    }
  }
]
```

#### Headers

This library allow strategy based on `Prefer` header see
official [PostgREST Documentation](https://postgrest.org/en/stable/references/api/preferences.html) by adding `@Header`
annotation over your Repository

```java
import fr.ouestfrance.querydsl.postgrest.PostgrestClient;
import fr.ouestfrance.querydsl.postgrest.annotations.Header;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import fr.ouestfrance.querydsl.postgrest.model.Prefer;

import static fr.ouestfrance.querydsl.postgrest.annotations.Header.Method.UPSERT;

@PostgrestConfiguration(resource = "posts")
// Return representation object for all functions
@Header(key = Prefer.HEADER, value = Prefer.Return.REPRESENTATION)
// Make Upsert using POST with Merge_Duplicated value
@Header(key = Prefer.HEADER, value = Prefer.Resolution.MERGE_DUPLICATES, methods = UPSERT)
public class PostRepository extends PostgrestRepository<Post> {

    public PostRepository(PostgrestClient client) {
        super(client);
    }
}
```
##### Upserts

If you want to specify which properties form your unique constraint, you don't need to write the
`resolution=merge-duplicates` header anymore :

```java
@Header(key = Prefer.HEADER, value = Prefer.Resolution.MERGE_DUPLICATES, methods = UPSERT)
@PostgrestConfiguration(resource = "posts")
public class PostRepository extends PostgrestRepository<Post> {

    public PostRepository(PostgrestClient client) {
        super(client);
    }
}
```

Just annotate your implementation of Repository with `@OnConflict` and specify which fields form
your unique constraint :

```java
import fr.ouestfrance.querydsl.postgrest.annotations.OnConflict;

@OnConflict(columnNames = {"codeOrigine", "referencePersonne", "uuidAdresse"})
@PostgrestConfiguration(resource = "posts")
public class PostRepository extends PostgrestRepository<Post> {

    public PostRepository(PostgrestClient client) {
        super(client);
    }
}
```

#### Logical condition

Any chance you want to have a more complex condition, it's possible to make mixin or / and condition by
using `groupName`

```java
import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterFields;
import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Select(alias = "filterFormats", value = "formats!inner(minSize, maxSize)")
public class PostRequestWithSize {

    // size = $size OR (filterFormats.minSize < size AND filterFormats.maxSize > size)
    @FilterField(key = "size", groupName = "sizeOrGroup")
    @FilterFields(groupName = "sizeOrGroup", value = {
            @FilterField(key = "filterFormats.minSize", operation = FilterOperation.GTE.class),
            @FilterField(key = "filterFormats.maxSize", operation = FilterOperation.LTE.class, orNull = true)
    })
    private String size;
}
```

or on multiple fields

```java
import fr.ouestfrance.querydsl.FilterField;

public class PostRequestWithAuthorOrSubject {

    // subject = $subject OR name= $name
    @FilterField(groupName = "subjectOrAuthorName")
    private String subject;

    @FilterField(groupName = "subjectOrAuthorName")
    private String name;

}

```

#### PostgrestFilterOperation

extends FilterOperation with

| Operator | Description                                | Field type    |
|----------|---------------------------------------------|---------------|
| ILIKE    | Case-insensitive LIKE                        | `String`      |
| CS       | Contains for JSON/Range datatype             | `String`      |
| CD       | Contained for JSON/Range datatype            | `String`      |
| OV       | Overlap (ranges having points in common)     | `HasRange<T>` |
| SL       | Strictly left of                             | `HasRange<T>` |
| SR       | Strictly right of                            | `HasRange<T>` |
| NXR      | Does not extend to the right of              | `HasRange<T>` |
| NXL      | Does not extend to the left of               | `HasRange<T>` |
| ADJ      | Is adjacent to                               | `HasRange<T>` |

```java
import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Range;

import java.time.LocalDate;

public class PeriodSearch {
    @FilterField(operation = PostgrestFilterOperation.OV.class, key = "period")
    Range<LocalDate> period; // period=ov.[2024-01-01,2024-06-30]
}
```

#### Bulk Operations

PostgREST allow to execute operations over a wide range items.
QueryDSL-Postgrest allow to handle pagination fixed by user or fixed by the postgREST max page

```java
import fr.ouestfrance.querydsl.postgrest.model.BulkOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public void invalidatePassword() {
        UserSearch criteria = new UserSearch();
        // Will invalidate all passwords with chunk of 1000 users
        userRepository.patch(criteria, new UserPatchPassword(false), BulkOptions.builder()
                .countOnly(true)
                .pageSize(1000)
                .build());
        // Generate n calls of
        // PATCH /users {"password_validation": false }  -H Range-Unit items
        // PATCH /users {"password_validation": false }  -H Range 1000-1999
        // PATCH /users {"password_validation": false }  -H Range 2000-2999
        // etc since the users are all updated
    }
}
```

The first call carries the `Range-Unit` header only, the next ones page through the remaining items.

| Option    | Default Value | Description                                                               |
|-----------|---------------|---------------------------------------------------------------------------|
| countOnly | false         | Place return=headers-only if true, otherwise keep default return          |  
| pageSize  | -1            | Specify the size of the chunk, otherwise let postgrest activate its limit |

> Bulk Operations are allowed on  `Post` ,`Patch`, `Delete` and `Upsert`

#### Rpc Calls 

Supports of [rpc function calls](https://postgrest.org/en/v12/references/api/functions.html#functions-as-rpc)

**Configuration**
Its use a PostgrestRpcClient which use the PostgrestClient

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.PostgrestClient;
import fr.ouestfrance.querydsl.postgrest.PostgrestWebClient;
import fr.ouestfrance.querydsl.postgrest.PostgrestRpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PostgrestConfiguration {

    @Bean
    public PostgrestRpcClient rpcClient() {
        String serviceUrl = "http://localhost:9000";
        WebClient webclient = WebClient.builder()
                .baseUrl(serviceUrl)
                // Here you can add any filters or default configuration you want
                .build();

        return new PostgrestRpcClient(PostgrestWebClient.of(webclient));
    }
}
```

then you can call your rpc method using this call 

Every `executeRpc` overload returns an `Optional` of the requested class.

```java
import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.PostgrestRpcClient;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;

import java.util.Arrays;
import java.util.List;

public class Example {
    private PostgrestRpcClient rpcClient;

    public List<Coordinate> getCoordinates() {
        // call getCoordinates_v1 and expect to return a list of Coordinates
        return rpcClient.executeRpc("getCoordinates_v1", Coordinate[].class)
                .map(Arrays::asList)
                .orElseGet(List::of);
        // CALL => ${base_url}/rpc/getCoordinates_v1
    }

    public Coordinate getCoordinate(Point point) {
        // call findClosestCoordinate_v1 with body {x:?, y:?} 
        // expect to return a single coordinate
        return rpcClient.executeRpc("findClosestCoordinate_v1", point, Coordinate.class)
                .orElseThrow();
        // CALL => ${base_url}/rpc/findClosestCoordinate_v1
        // => with body {x: point.x, y: point.y}
    }

    public SimpleCoordinate getCoordinateX(Point point) {
        // call findClosestCoordinate_v1 with body {x:?, y:?}
        // add Criteria that add select=x,y and z=gte.0.0
        // and return the result as a SimpleCoordinate class
        return rpcClient.executeRpc("findClosestCoordinate_v1", new CoordinateCriteria(0.0f), point,
                        SimpleCoordinate.class)
                .orElseThrow();
        // CALL => ${base_url}/rpc/findClosestCoordinate_v1?z=gte.0.0&select=x,y
        // => with body {x: point.x, y: point.y}
    }


    @Select({"x", "y"})
    record CoordinateCriteria(
            @FilterField(key = "z", operation = FilterOperation.GTE.class)
            Float z
    ) {}

    record SimpleCoordinate(Float x, Float y) {}
}
```

To get a parameterized return type such as a `List<Coordinate>` without going through an array, the
lower level `PostgrestClient.rpc(String, Map, Object, Type)` accepts a `java.lang.reflect.Type`.

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
