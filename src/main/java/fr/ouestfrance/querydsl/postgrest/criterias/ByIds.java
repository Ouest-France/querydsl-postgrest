package fr.ouestfrance.querydsl.postgrest.criterias;

import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterOperation;

import java.io.Serializable;
import java.util.List;

record ByIds(@FilterField(operation = FilterOperation.IN)
             List<Comparable<?>> id) {

}
