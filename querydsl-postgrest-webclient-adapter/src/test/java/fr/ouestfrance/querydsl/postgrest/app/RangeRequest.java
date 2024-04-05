package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Range;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RangeRequest {


    @FilterField(operation = FilterOperation.BETWEEN.class)
    private Range<LocalDate> birthDate;

    @FilterField(operation = FilterOperation.BETWEEN.class)
    private Range<Integer> siblings;

}
