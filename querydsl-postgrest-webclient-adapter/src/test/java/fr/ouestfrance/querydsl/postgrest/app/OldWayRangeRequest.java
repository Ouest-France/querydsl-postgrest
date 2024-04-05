package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OldWayRangeRequest {


    @FilterField(operation = FilterOperation.GTE.class, key = "birthDate")
    private LocalDate minBirthDate;

    @FilterField(operation = FilterOperation.LTE.class, key = "birthDate")
    private LocalDate maxBirthDate;

    @FilterField(operation = FilterOperation.GTE.class, key = "siblings")
    private Integer minSiblings;

    @FilterField(operation = FilterOperation.LTE.class, key = "siblings")
    private Integer maxSiblings;

}
