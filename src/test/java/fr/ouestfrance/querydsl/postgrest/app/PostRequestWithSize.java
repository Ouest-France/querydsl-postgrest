package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterFields;
import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Select(alias = "filterFormats", value = "formats!inner(minSize, maxSize)")
public class PostRequestWithSize {

    // size = $size OR (minSize < size AND maxSize > size)
    @FilterField(key = "size", groupName = "sizeOrGroup")
    @FilterFields(groupName = "sizeOrGroup", value = {
            @FilterField(key = "filterFormats.minSize", operation = FilterOperation.GTE),
            @FilterField(key = "filterFormats.maxSize", operation = FilterOperation.LTE, orNull = true)
    })
    private String size;
}

