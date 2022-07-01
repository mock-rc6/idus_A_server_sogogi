package com.example.demo.src.classes.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class BestReview {
    private long onlineClassReviewId;
    private String reviewImg;
    private int rating;
    private String nickName;
    private String contents;
    private long classId;
    private String classImg;
    private String classTitle;
}
