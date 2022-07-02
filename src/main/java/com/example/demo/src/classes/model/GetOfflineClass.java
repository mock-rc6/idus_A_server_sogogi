package com.example.demo.src.classes.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class GetOfflineClass {

    public GetOfflineClass(String categoryName, String title, int price, int discountRate, int finalPrice,
                           String level, String timeSpend, String maxCapacity, boolean userLike, int countLike,
                           long writerId, String writerImg, String writerName, String contents, String addressName) {
        this.categoryName = categoryName;
        this.title = title;
        this.price = price;
        this.discountRate = discountRate;
        this.finalPrice = finalPrice;
        this.level = level;
        this.timeSpend = timeSpend;
        this.maxCapacity = maxCapacity;
        this.userLike = userLike;
        this.countLike = countLike;
        this.writerId = writerId;
        this.writerImg = writerImg;
        this.writerName = writerName;
        this.contents = contents;
        this.addressName = addressName;
    }

    List<String> classImgList;
    private String categoryName;
    private String title;
    private int price;
    private int discountRate;
    private int finalPrice;
    private String level;
    private String timeSpend;
    private String maxCapacity;
    private boolean userLike;
    private int countLike;
    private long writerId;
    private String writerImg;
    private String writerName;
    private String contents;
    private String addressName;
    private List<ClassReviews> classReviewsList;
    private List<ClassComment> classCommentList;
}
