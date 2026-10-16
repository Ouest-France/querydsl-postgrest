package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Select({"title", "userId"})
public class PostRequestWithSelect {

    @FilterField
    private Integer userId;
    @FilterField(operation = FilterOperation.NEQ.class)
    private Integer id;
}
