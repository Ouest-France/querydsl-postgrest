package fr.ouestfrance.querydsl.postgrest.criterias;

import java.util.List;

import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterOperation;

record ByIds(@FilterField(operation = FilterOperation.IN.class)
             List<Comparable<?>> id) {

}
