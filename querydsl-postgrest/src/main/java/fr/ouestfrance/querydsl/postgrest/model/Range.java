package fr.ouestfrance.querydsl.postgrest.model;

import fr.ouestfrance.querydsl.service.ext.HasRange;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Range<T> implements HasRange<T> {

    private T lower;
    private T upper;
    private boolean lowerInclusive;
    private boolean upperInclusive;

    public static <T> Range<T> between(T from, T to) {
        return new Range<>(from, to, true, true);
    }

    public static <T> Range<T> exclusiveBetween(T from, T to) {
        return new Range<>(from, to, false, false);
    }
}
