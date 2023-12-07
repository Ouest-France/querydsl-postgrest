package fr.ouestfrance.querydsl.postgrest.criterias;

import fr.ouestfrance.querydsl.FilterField;

record ById(@FilterField Comparable<?> id) {
}
