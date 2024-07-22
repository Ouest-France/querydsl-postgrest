package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.postgrest.PostgrestClient;
import fr.ouestfrance.querydsl.postgrest.PostgrestRepository;
import fr.ouestfrance.querydsl.postgrest.annotations.Header;
import fr.ouestfrance.querydsl.postgrest.annotations.OnConflict;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;
import fr.ouestfrance.querydsl.postgrest.model.Prefer;

@PostgrestConfiguration(resource = "posts")
@Select("authors(*)")
@Header(key = Prefer.HEADER, value = Prefer.Return.REPRESENTATION)
@OnConflict(columnNames = {"id", "title"})
public class PostRepository extends PostgrestRepository<Post> {

    public PostRepository(PostgrestClient client) {
        super(client);
    }

}
