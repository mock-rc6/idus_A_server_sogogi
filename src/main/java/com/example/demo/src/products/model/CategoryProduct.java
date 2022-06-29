package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class CategoryProduct {
    private long categoryId;
    private String categoryName;
    private List<Products> productsList;
}
