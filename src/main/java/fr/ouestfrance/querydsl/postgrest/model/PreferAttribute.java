package fr.ouestfrance.querydsl.postgrest.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Attributes used by postgRest.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public enum PreferAttribute {

	/**
	 * To obtain the exact number of rows.
	 */
	COUNT_EXACT("count=exact"),
	/**
	 * To get a fairly accurate and fast count.
	 */
	COUNT_PLANNED("count=planned"),
	/**
	 * To get an estimated count when number of rows exeed the define db-max-rows,
	 * otherwise you get the exact number of rows.
	 */
	COUNT_ESTIMATED("count=estimated"),
	/**
	 * To embed related resources after an operation. You have to define a select param.
	 * Example : /films?select=title,year,director:directors(first_name,last_name)
	 */
	RETURN_REPRESENTATION("return=representation"),
	/**
	 * To return a Location header describing where to find the new/updated object.
	 */
	RETURN_HEADER_ONLY("return=headers-only"),
	/**
	 * Use to upsert on post.
	 */
	RESOLUTION_MERGE_DUPLICATE("resolution=merge-duplicates"),
	/**
	 * Use to avoid creation if already exists on post.
	 */
	RESOLUTION_IGNORE_DUPLICATE("resolution=ignore-duplicates"),
	/**
	 * To call a function that takes a single parameter of type JSON.
	 */
	PARAMS_SINGLE_OBJECT("params=single-object"),
	/**
	 * To call a function in a bulk way.
	 */
	PARAMS_MULTIPLE_OBJECT("params=multiple-objects"),
	/**
	 * To avoid change committing when using the analyse option.
	 */
	TX_ROLLBACK("tx=rollback");

	private final String attribute;

}
