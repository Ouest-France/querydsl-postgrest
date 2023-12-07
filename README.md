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

QueryDsl postgrest provides class to simplify querying postgrest api using PostgrestClient,
It actually provides by default WebClient adapter `PostgrestWebClient` adapter.

It's really easy to create your own HttpClientAdapter (RestTemplate, OkHttpClient, HttpConnexion, ...) by implementing `PostgrestClient` interface.

You can also specify authenticators, interceptors (retry, transform) and every configuration (timeout, default headers,
cookies, ...) you need to deploy.

**Webclient configuration example**

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
public class UserSearch {
    @FilterField
    String id;
    @FilterField(operation = FilterOperation.LIKE)
    String name;
}
```

*@Since 1.1.0 - Record Support*
```java
public record UserSearch(
        @FilterField String id,
        @FilterField(operation = FilterOperation.LIKE) String name
){}
```


#### Create your repository

To access data, you have to create Repository for your type and put `@PostgrestConfiguration` to specify extra data

| Property      | Required | Format | Description                                                 | Example         |
|---------------|----------|--------|-------------------------------------------------------------|-----------------|
| resource      | O        | String | Resource name in the postgrest api                         | "users"         |
| countStrategy | X        | String | Count strategy (exact, planned, estimated) default is exact | CountType.EXACT |

```java
import fr.ouestfrance.querydsl.postgrest.PostgrestClient;

@Repository
@PostgrestConfiguration(resource = "users")
public class UserRepository extends PostgrestRepository<User> {

    public UserRepository(PostgrestClient client) {
        super(client);
    }

}
```

##### PostgrestRepository functions

| Method  | Return        | Parameters                                                  | Description                                                                                                                |
|---------|---------------|-------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------|
| search  | `Page<T>`     | criteria : `Object`<br/>pageRequest : `Pageable` (optional) | Search request based on criteria and pagination                                                                            |
| findOne | `Optional<T>` | criteria : `Object`                                         | find one entity based on criteria<br/>Raise `PostgrestRequestException` if criteria return more than one item              |
| getOne  | `T`           | criteria : `Object`                                         | get one entity based on criteria<br/>Raise `PostgrestRequestException` if criteria returned no entity                      |
| upsert  | `T`           | value : `Object`                                            | post data, you may define the strategy (Insert / Update) by adding header annotation `Prefer: resolution=merge-duplicates` |
| upsert  | `List<T>`     | value : `List<Object>`                                      | post data, you may define the strategy (Insert / Update) by adding header annotation `Prefer: resolution=merge-duplicates` |
| update  | `List<T>`     | criteria : `Object`<br/>value: `Object`                     | Update entities found by criterias                                                                                         |
| delete  | `List<T>`     | criteria : `Object`                                         | Delete entities found by the criteria                                                                                      |

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
        return userRepository.search(search);
    }
}
```

### Advanced features


#### Vertical filtering

When certain columns are wide (such as those holding binary data), it is more efficient for the server to withhold them
in a response. The client can specify which columns are required using the select parameter.
This can be defined by annotation `@Select`

Select annotation can be added on the Repository but also to the criteria object that allow you to add specific selection for filtering

| Property | Required | Format | Description              | Example     |
|----------|----------|--------|--------------------------|-------------|
| value    | O        | String | select value tu add      | "firstname" |
| alias    | X        | String | renaming column or alias | "fullName"  |

You can add extra selection by adding `@Select` annotation.
In this example there is an inner join on `Posts.author` and selecting only `firstName` and `lastName`

```java
@PostgrestConfiguration(resource = "posts")
@Select(alias="author", value="author!inner(firstName, lastName)")
public class PostRepository extends PostgrestRepository<Post> {
    
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

This library allow strategy based on `Prefer` header see official [PostgREST Documentation](https://postgrest.org/en/stable/references/api/preferences.html) by adding `@Header` annotation over your Repositoru

```java
// Return representation object for all functions
@Header(key = Prefer.HEADER, value = Prefer.Return.REPRESENTATION)
// Make Upsert using POST with Merge_Duplicated value
@Header(key = Prefer.HEADER, value = Prefer.Resolution.MERGE_DUPLICATES, methods = UPSERT)
public class PostRepository extends PostgrestRepository<Post> {
}
```

#### Logical condition

Any chance you want to have a more complex condition, it's possible to make mixin or / and condition by using `groupName`

```java

@Getter
@Setter
@Select(alias = "filterFormats", value = "formats!inner(minSize, maxSize)")
public class PostRequestWithSize {

    // size = $size OR (filterFormats.minSize < size AND filterFormats.maxSize > size)
    @FilterField(key = "size", groupName = "sizeOrGroup")
    @FilterFields(groupName = "sizeOrGroup", value = {
            @FilterField(key = "filterFormats.minSize", operation = FilterOperation.GTE),
            @FilterField(key = "filterFormats.maxSize", operation = FilterOperation.LTE, orNull = true)
    })
    private String size;
}
```
or on multiple fields 

```java
public class PostRequestWithAuthorOrSubject {

    // subject = $subject OR name= $name
    @FilterField(groupName = "subjectOrAuthorName")
    private String subject;

    @FilterField(groupName = "subjectOrAuthorName")
    private String name;

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