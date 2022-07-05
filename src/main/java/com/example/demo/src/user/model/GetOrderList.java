package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GetOrderList {
    private long productId;
    private String orderAt;
    private int finalPrice;
    private String productImg;
    private String title;
    private String writerName;
    private String sendStatus;
}
