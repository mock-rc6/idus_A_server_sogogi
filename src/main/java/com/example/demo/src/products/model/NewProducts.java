package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class NewProducts {

    public NewProducts(long productId, boolean isLike, String imgUrl) {
        this.productId = productId;
        this.isLike = isLike;
        this.imgUrl = imgUrl;
    }

    private long productId;
    private boolean isLike;
    private String imgUrl;
    private String writerName;
    private String title;
    private int price;
    private int discountRate;
    private int finalPrice;
    private boolean isFreeDelivery;
}
