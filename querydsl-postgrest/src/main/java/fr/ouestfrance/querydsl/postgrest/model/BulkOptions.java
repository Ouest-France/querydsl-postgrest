package fr.ouestfrance.querydsl.postgrest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BulkOptions {
    private boolean countOnly = false;
    private int pageSize = -1;
}
