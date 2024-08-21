package fr.ouestfrance.querydsl.postgrest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Range object
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HeaderRange {

    /**
     * Range regexp
     */
    @SuppressWarnings("java:S5852")
    private static final Pattern REGEXP = Pattern.compile("(?<offset>\\d+)-(?<limit>\\d+)/(?<total>[*\\d]+)");
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
    public static HeaderRange of(String rangeString) {
        Matcher matcher = REGEXP.matcher(rangeString);
        HeaderRange range = new HeaderRange();
        if (matcher.find()) {
            range.offset = Integer.parseInt(matcher.group("offset"));
            range.limit = Integer.parseInt(matcher.group("limit"));
            String total = matcher.group("total");
            if (total.startsWith("*")) {
                range.totalElements = range.limit - range.offset + 1;
            } else {
                range.totalElements = Long.parseLong(total);
            }
        }
        return range;
    }

    /**
     * Get count
     *
     * @return count
     */
    public long getCount() {
        return totalElements == 0 ? 0 : limit - offset + 1;
    }
}
