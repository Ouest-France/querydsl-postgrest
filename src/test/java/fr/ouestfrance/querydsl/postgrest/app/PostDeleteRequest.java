package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDeleteRequest {

    @FilterField(operation = FilterOperation.IN)
    private List<String> id;

}
