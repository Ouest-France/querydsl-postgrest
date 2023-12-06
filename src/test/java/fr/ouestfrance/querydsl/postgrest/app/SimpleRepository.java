package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.postgrest.PostgrestWebClient;
import fr.ouestfrance.querydsl.postgrest.PostgrestRepository;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;

@PostgrestConfiguration(resource = "posts")
public class SimpleRepository extends PostgrestRepository<Post> {
    public SimpleRepository(PostgrestWebClient client) {
        super(client);
    }
}
