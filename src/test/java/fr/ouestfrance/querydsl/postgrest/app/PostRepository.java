package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.postgrest.PostgrestClient;
import fr.ouestfrance.querydsl.postgrest.PostgrestWebClient;
import fr.ouestfrance.querydsl.postgrest.PostgrestRepository;
import fr.ouestfrance.querydsl.postgrest.annotations.Header;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;

import static fr.ouestfrance.querydsl.postgrest.annotations.Header.Method.UPSERT;

@PostgrestConfiguration(resource = "posts")
@Select("authors(*)")
@Header(key = "Prefer", value = "return=representation")
@Header(key = "Prefer", value = {"tx=rollback", "resolution=merge-duplicates"}, methods = UPSERT)
public class PostRepository extends PostgrestRepository<Post> {

    public PostRepository(PostgrestClient client) {
        super(client);
    }

}
