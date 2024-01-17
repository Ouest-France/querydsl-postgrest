package fr.ouestfrance.querydsl.postgrest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class that allow to handle querystring
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueryStringUtils {

    /**
     * Allow to transform a Multimap value to queryString
     * @param multimap multimap to transform
     * @return query string representation
     */
    public static String toQueryString(Map<String, List<String>> multimap){
        List<String> queryList = new ArrayList<>();
        multimap.forEach((key,values)-> values.forEach(value-> queryList.add(key+"="+value)));
        return String.join("&", queryList);
    }

}
