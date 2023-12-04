package fr.ouestfrance.querydsl.postgrest.app;

import fr.ouestfrance.querydsl.FilterField;
import fr.ouestfrance.querydsl.FilterFields;
import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.annotations.Select;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Select(alias = "author", value = "authors!inner(name)")
public class PostRequestWithAuthorOrSubject {

    // subject = $subject OR author.name= $author
    @FilterField(groupName = "subjectOrAuthorName")
    private String subject;

    @FilterField(key = "author.name", groupName = "subjectOrAuthorName")
    private String author;

}

