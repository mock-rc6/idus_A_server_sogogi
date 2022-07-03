package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GetCategoryProduct {

    public GetCategoryProduct(long productId, String productImg, boolean userLike) {
        this.productId = productId;
        this.productImg = productImg;
        this.userLike = userLike;
    }

    private long productId;
    private String productImg;
    private boolean userLike;
    private String writerName;
    private String title;
    private int price;
    private int discountRate;
    private int finalPrice;
    private double rating;
    private int countReview;
    private String lastReview;
}
