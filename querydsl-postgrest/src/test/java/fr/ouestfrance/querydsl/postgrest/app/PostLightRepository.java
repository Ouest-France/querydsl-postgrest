package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.postgrest.PostgrestClient;
import fr.ouestfrance.querydsl.postgrest.PostgrestRepository;
import fr.ouestfrance.querydsl.postgrest.annotations.Header;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;
import fr.ouestfrance.querydsl.postgrest.model.Prefer;

@PostgrestConfiguration(resource = "posts")
@Select(value = {"userId","id", "title", "body"},only = true)
@Header(key = Prefer.HEADER, value = Prefer.Return.REPRESENTATION)
public class PostLightRepository extends PostgrestRepository<Post> {

    public PostLightRepository(PostgrestClient client) {
        super(client);
    }

}
