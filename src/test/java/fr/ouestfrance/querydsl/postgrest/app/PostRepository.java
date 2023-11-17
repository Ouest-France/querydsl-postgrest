package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.postgrest.PostgrestRepository;
import fr.ouestfrance.querydsl.postgrest.PostgrestConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

@PostgrestConfiguration(resource = "posts", embedded = "authors")
public class PostRepository extends PostgrestRepository<Post> {

}
