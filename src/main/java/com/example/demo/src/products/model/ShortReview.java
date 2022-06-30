package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ShortReview {
    private boolean repurchase;
    private String imgUrl;
    private String contents;
}
