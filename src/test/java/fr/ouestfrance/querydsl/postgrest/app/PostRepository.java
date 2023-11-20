package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.postgrest.PostgrestRepository;
import fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration;
import fr.ouestfrance.querydsl.postgrest.model.Header;

@PostgrestConfiguration(
        resource = "posts",
        embedded = "authors",
        deleteHeaders = @Header(key = "Prefer", value = "return=representation"),
        upsertHeaders = @Header(key = "Prefer", value = {"return=representation", "tx=rollback", "resolution=merge-duplicates"}),
        patchHeaders = @Header(key = "Prefer", value = "return=representation")
)
public class PostRepository extends PostgrestRepository<Post> {

}
