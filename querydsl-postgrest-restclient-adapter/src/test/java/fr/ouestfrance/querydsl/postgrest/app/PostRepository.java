package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.postgrest.PostgrestClient;
import fr.ouestfrance.querydsl.postgrest.PostgrestRepository;
import fr.ouestfrance.querydsl.postgrest.annotations.*;
import fr.ouestfrance.querydsl.postgrest.model.Prefer;

import static fr.ouestfrance.querydsl.postgrest.annotations.Header.Method.UPSERT;

@PostgrestConfiguration(resource = "posts")
@Select("authors(*)")
@Header(key = Prefer.HEADER, value = Prefer.Return.REPRESENTATION)
@OnConflict(columnNames = {"id", "title"})
public class PostRepository extends PostgrestRepository<Post> {

    public PostRepository(PostgrestClient client) {
        super(client);
    }

}
