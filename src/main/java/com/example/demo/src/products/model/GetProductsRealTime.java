package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GetProductsRealTime {

    public GetProductsRealTime(String lookUpTime) {
        this.lookUpTime = lookUpTime;
    }

    private String lookUpTime;
    private List<RealTimeProducts> realTimeProducts;
}