package com.example.demo.src.classes.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserLikeClasses {
    private long classId;
    private String profileImg;
    private String categoryName;
    private String writerName;
    private String classTitle;
}
