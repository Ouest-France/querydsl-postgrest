package fr.ouestfrance.querydsl.postgrest.model;

import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Range object
 */
@Getter
public class Range {

    /**
     * Range regexp
     */
    @SuppressWarnings("java:S5852")
    private static final Pattern REGEXP = Pattern.compile("(?<offset>\\d+)-(?<limit>\\d+)/(?<total>\\d+)");
    /**
     * Start of the range
     */
    private int offset;
    /**
     * Limit of the range
     */
    private int limit;
    /**
     * Total elements
     */
    private long totalElements;

    /**
     * Range string format is "{offset}-{limit}/{totalElements}
     *
     * @param rangeString string representation of range
     * @return range object
     */
    public static Range of(String rangeString) {
        Matcher matcher = REGEXP.matcher(rangeString);
        Range range = new Range();
        if (matcher.find()) {
            range.offset = Integer.parseInt(matcher.group("offset"));
            range.limit = Integer.parseInt(matcher.group("limit"));
            range.totalElements = Long.parseLong(matcher.group("total"));
        }
        return range;
    }
}
