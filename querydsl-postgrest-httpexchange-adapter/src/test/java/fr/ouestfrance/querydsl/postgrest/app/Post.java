package fr.ouestfrance.querydsl.postgrest.app;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Post {

    private Integer userId;
    private String id;
    private String title;
    private String body;
}
