package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GetLikeProduct {
    private long productId;
    private String imgUrl;
    private String writerName;
    private String title;
    private int price;
    private int discountRate;
    private int finalPrice;
    private double rating;
    private int countReview;
    private String lastReview;
}
