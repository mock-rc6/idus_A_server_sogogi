package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GetLikeOnlineClasses {
    private long onlineClassId;
    private String classImg;
    private String categoryName;
    private String level;
    private String title;
    private String writerName;
}
