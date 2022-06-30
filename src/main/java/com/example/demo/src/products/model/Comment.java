package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Comment {
    private String userName;
    private String userProfileImg;
    private String userComment;
    private String writerName;
    private String writerProfileImg;
    private String writerComment;
}
