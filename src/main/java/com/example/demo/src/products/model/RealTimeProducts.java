package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class RealTimeProducts {

    public RealTimeProducts(Long productId, boolean isLike, String imgUrl) {
        this.productId = productId;
        this.isLike = isLike;
        this.imgUrl = imgUrl;
    }

    private Long productId;
    private boolean isLike;
    private String imgUrl;
    private String writerName;
    private String title;
    private Double rating;
    private Integer countReview;
    private String reviewContents;
}
