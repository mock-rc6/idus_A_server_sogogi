package com.example.demo.src.classes.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ClassReviews {
    private long reviewId;
    private String reviewImg;
    private String userName;
    private String profileImg;
    private String createAt;
    private int rating;
    private String contents;
}
