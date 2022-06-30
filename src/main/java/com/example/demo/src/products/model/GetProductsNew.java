package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GetProductsNew {

    public GetProductsNew(String lookUpTime) {
        this.lookUpTime = lookUpTime;
    }

    private String lookUpTime;
    private List<NewProducts> newProductsList;
}
