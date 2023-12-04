package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.postgrest.PostgrestRepository;
import fr.ouestfrance.querydsl.postgrest.annotations.Header;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;

@PostgrestConfiguration(resource = "posts")
public class SimpleRepository extends PostgrestRepository<Post> {

}
