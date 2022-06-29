package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GetProductsRes {

    public GetProductsRes() {
    }

    private List<CategoryProduct> categoryProductList;
    private List<ProductReview> productReviewList;
}
