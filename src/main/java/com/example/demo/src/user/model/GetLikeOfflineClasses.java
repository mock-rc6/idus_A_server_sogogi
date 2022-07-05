package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GetLikeOfflineClasses {
    private long offlineClassId;
    private String classImg;
    private String addressName;
    private String categoryName;
    private String title;
    private int price;
}
