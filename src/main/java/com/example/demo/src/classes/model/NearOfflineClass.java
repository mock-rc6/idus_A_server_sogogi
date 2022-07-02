package com.example.demo.src.classes.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class NearOfflineClass {
    private long offlineClassId;
    private String imgUrl;
    private String addressName;
    private String categoryName;
    private String title;
    private Double rating;
    private int countReview;
}
