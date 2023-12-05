package fr.ouestfrance.querydsl.postgrest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.PostgrestClient;
import fr.ouestfrance.querydsl.postgrest.PostgrestRepository;
import fr.ouestfrance.querydsl.postgrest.annotations.Header;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;

@PostgrestConfiguration(resource = "posts")
public class SimpleRepository extends PostgrestRepository<Post> {
    public SimpleRepository(PostgrestClient client, ObjectMapper mapper) {
        super(client, mapper);
    }
}
