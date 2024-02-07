package fr.ouestfrance.querydsl.postgrest.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class CountItem extends HashMap<String, String> {

    public static CountItem of(int count) {
        CountItem countItem = new CountItem();
        countItem.put("count", String.valueOf(count));
        return countItem;
    }
}
