/**
 * Jackson support for the PostgREST range format.
 *
 * <p>PostgreSQL range columns ({@code daterange}, {@code int4range}, {@code tsrange}, ...) are
 * exposed by PostgREST as a single string literal rather than as an object. This package maps that
 * literal to and from {@link fr.ouestfrance.querydsl.postgrest.model.Range}, so that a field
 * declared as {@code Range<LocalDate>} reads and writes {@code "[2026-05-21,2026-09-01)"} instead
 * of {@code {"lower":"2026-05-21","upper":"2026-09-01",...}}.
 *
 * <h2>Format</h2>
 *
 * <p>A range literal is an opening bracket, a lower bound, a comma, an upper bound and a closing
 * bracket. A square bracket marks an inclusive bound, a round one an exclusive bound, and an empty
 * bound means unbounded on that side. Whitespace around a bound is tolerated when reading.
 *
 * <table border="1">
 *   <caption>Accepted literals</caption>
 *   <tr><th>Literal</th><th>Lower</th><th>Upper</th><th>Meaning</th></tr>
 *   <tr><td>{@code [2026-05-21,2026-09-01)}</td><td>2026-05-21, inclusive</td><td>2026-09-01, exclusive</td><td>both bounds</td></tr>
 *   <tr><td>{@code (1,5]}</td><td>1, exclusive</td><td>5, inclusive</td><td>both bounds</td></tr>
 *   <tr><td>{@code [2026-05-21,)}</td><td>2026-05-21, inclusive</td><td>{@code null}</td><td>no upper bound</td></tr>
 *   <tr><td>{@code (,2026-09-01]}</td><td>{@code null}</td><td>2026-09-01, inclusive</td><td>no lower bound</td></tr>
 *   <tr><td>{@code [,)}</td><td>{@code null}</td><td>{@code null}</td><td>fully unbounded</td></tr>
 * </table>
 *
 * <p>Anything else is rejected with an {@link java.lang.IllegalArgumentException} : a literal
 * without brackets ({@code 2026-05-21,2026-09-01}), without a comma ({@code [2026-05-21]}), or
 * holding more than two bounds ({@code [1,2,3)}).
 *
 * <h2>Bound conversion</h2>
 *
 * <p>Bounds are never parsed nor formatted by this package. The bound type {@code T} is resolved
 * from the generic type declared on the field ({@code Range<LocalDate>}, {@code Range<Integer>},
 * ...) and the conversion is delegated to the Jackson serializer and deserializer already
 * registered for that type. Any type Jackson can handle as a JSON string is therefore usable as a
 * bound, and a custom (de)serializer registered for it is honored here too. When the generic type
 * is not available, bounds fall back to {@link java.lang.String}.
 *
 * <p>{@link fr.ouestfrance.querydsl.postgrest.jackson.RangeSerializer} and
 * {@link fr.ouestfrance.querydsl.postgrest.jackson.RangeDeserializer} are reciprocal : reading a
 * literal and writing it back yields the same literal.
 *
 * <h2>Registration</h2>
 *
 * <p>{@link fr.ouestfrance.querydsl.postgrest.jackson.PostgrestRangeModule} is declared in
 * {@code META-INF/services/tools.jackson.databind.JacksonModule}, so a mapper built with
 * {@code findAndAddModules()} picks it up with no configuration :
 *
 * <pre>{@code
 * JsonMapper mapper = JsonMapper.builder().findAndAddModules().build();
 * }</pre>
 *
 * <p>Otherwise register the module explicitly :
 *
 * <pre>{@code
 * JsonMapper mapper = JsonMapper.builder().addModule(new PostgrestRangeModule()).build();
 * }</pre>
 *
 * <p>Either way the module applies to every {@code Range} handled by that mapper, including the
 * ones exposed by an application's own API.
 */
package fr.ouestfrance.querydsl.postgrest.jackson;
