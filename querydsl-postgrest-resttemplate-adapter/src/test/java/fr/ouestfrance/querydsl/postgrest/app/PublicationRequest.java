package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Select(alias = "filtreDepartement", value = "editionsRegroupements_v1!inner(edition_v1!inner(departement_v1!inner(code)))")
@Select(alias = "filtreEditionsRegroupements", value = "editionsRegroupements_v1!inner(dateDebutValidite,dateFinValidite)")
@Select(alias = "filtrePublication", value = "publication_v1!inner(code,dateDebutValidite,dateFinValidite)")
public class PublicationRequest {

    @FilterField(key = "filtrePublication.code")
    String code;

    @FilterField(key = "filtrePublication.dateDebutValidite", operation = FilterOperation.LTE.class)
    @FilterField(key = "filtrePublication.dateFinValidite", operation = FilterOperation.GTE.class, orNull = true)
    @FilterField(key = "filtreEditionRegroupements.dateDebutValidite", operation = FilterOperation.LTE.class)
    @FilterField(key = "filtreEditionRegroupements.dateFinValidite", operation = FilterOperation.GTE.class, orNull = true)
    LocalDate dateValide;

    @FilterField(key = "filtreDepartement.edition_v1.departement_v1.code")
    String codeDepartement;

    @FilterField(key = "portee@>")
    String portee;
}
