package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Review {
    private long reviewId;
    private String nickName;
    private String profileImg;
    private int rating;
    private String createAt;
    private boolean repurchase;
    private String reviewImg;
    private String contents;
}
