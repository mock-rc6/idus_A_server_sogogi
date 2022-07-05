package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class ProductOption {
    private long optionId;
    private String optionName;
    private List<OptionDetail> optionDetailList;
}
