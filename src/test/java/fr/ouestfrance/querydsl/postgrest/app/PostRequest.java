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
public class PostRequest {

    @FilterField
    private Integer userId;
    @FilterField(operation = FilterOperation.NEQ)
    private Integer id;
    @FilterField(operation = FilterOperation.LIKE)
    private String title;
    @FilterField(operation = FilterOperation.GT, key = "birthDate")
    @FilterField(operation = FilterOperation.LTE, key = "startDate")
    @FilterField(operation = FilterOperation.GTE, key = "endDate", orNull = true)
    @FilterField(operation = FilterOperation.LT, key = "deathDate", orNull = true)
    private LocalDate validDate;

    @FilterField(operation = FilterOperation.IN, key = "status")
    private List<String> codes;
    @FilterField(operation = FilterOperation.NOT_IN, key = "status")
    private List<String> excludes;
}
