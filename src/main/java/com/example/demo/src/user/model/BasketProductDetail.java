package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class BasketProductDetail {
    private long basketDetailId;
    private long writerId;
    private String writerName;
    private String productImg;
    private String title;
    private String leftAmount;
    private List<BasketProductOption> basketProductOptionList;
    private int finalPrice;
    private int countOrder;
    private int deliveryFee;
    private int freeAmount;
}
