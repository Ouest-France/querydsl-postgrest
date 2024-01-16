package fr.ouestfrance.querydsl.postgrest.app;

import java.util.List;

import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDeleteRequest {

    @FilterField(operation = FilterOperation.IN.class)
    private List<String> id;

}
