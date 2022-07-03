package com.example.demo.src.products.model;

import lombok.Data;

@Data
public class RequestParams {
    private int img;
    private int free;
    private int sort; //0:인기순, 1:최신순, 2:낮은 가격순, 3:높은 가격순
    private int dis;
    private int min;
    private int max;
}
