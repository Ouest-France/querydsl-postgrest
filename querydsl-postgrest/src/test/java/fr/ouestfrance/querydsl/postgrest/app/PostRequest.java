package fr.ouestfrance.querydsl.postgrest.app;

import java.time.LocalDate;
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
public class PostRequest {

    @FilterField
    private Integer userId;
    @FilterField(operation = FilterOperation.NEQ.class)
    private Integer id;
    @FilterField(operation = FilterOperation.LIKE.class)
    private String title;
    @FilterField(operation = FilterOperation.GT.class, key = "birthDate")
    @FilterField(operation = FilterOperation.LTE.class, key = "startDate")
    @FilterField(operation = FilterOperation.GTE.class, key = "endDate", orNull = true)
    @FilterField(operation = FilterOperation.LT.class, key = "deathDate", orNull = true)
    private LocalDate validDate;

    @FilterField(operation = FilterOperation.IN.class, key = "status")
    private List<String> codes;
    @FilterField(operation = FilterOperation.NOTIN.class, key = "status")
    private List<String> excludes;
}
