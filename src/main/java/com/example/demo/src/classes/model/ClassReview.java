package com.example.demo.src.classes.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class ClassReview {
    private String userName;
    private String profileImg;
    private String createAt;
    private int rating;
    private String contents;
    private List<String> reviewImgList;
}
