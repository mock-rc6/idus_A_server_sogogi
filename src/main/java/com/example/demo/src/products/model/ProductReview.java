package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ProductReview {
    private long reviewId;
    private String reviewImg;
    private int rating;
    private String nickName;
    private String contents;
    private long productId;
    private String productImg;
    private String productTitle;
}
