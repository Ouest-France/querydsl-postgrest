package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.postgrest.model.Filter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LikeMapperTest {

    @Test
    void shouldCreateLike(){
        Filter filter = new LikeMapper().getFilter("complements->>nomFichierOrigine", "test.pdf*");
        Assertions.assertEquals("like.test.pdf*",filter.getFilterString());
        Assertions.assertEquals("complements->>nomFichierOrigine", filter.getKey());
    }

}
