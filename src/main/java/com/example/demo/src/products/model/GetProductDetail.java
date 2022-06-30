package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class GetProductDetail {

    public GetProductDetail(long writerId, String writerName, String profileImg, double rating, int countReview,
                            String title, int price, int discountRate, int finalPrice, int countBuyer, int deliveryFee,
                            int freeAmount, int leftAmount, String contents, double ratingAverage, int countAllReview,
                            int countProductLike) {
        this.writerId = writerId;
        this.writerName = writerName;
        this.profileImg = profileImg;
        this.rating = rating;
        this.countReview = countReview;
        this.title = title;
        this.price = price;
        this.discountRate = discountRate;
        this.finalPrice = finalPrice;
        this.countBuyer = countBuyer;
        this.deliveryFee = deliveryFee;
        this.freeAmount = freeAmount;
        this.leftAmount = leftAmount;
        this.contents = contents;
        this.ratingAverage = ratingAverage;
        this.countAllReview = countAllReview;
        this.countProductLike = countProductLike;
    }

    private List<String> imgUrlList;
    private long writerId;
    private String writerName;
    private String profileImg;
    private double rating;
    private int countReview;
    private String title;
    private int price;
    private int discountRate;
    private int finalPrice;
    private List<ShortReview> shortReviewList;
    private int countBuyer;
    private int deliveryFee;
    private int freeAmount;
    private int leftAmount;
    private String contents;
    private List<Review> reviewList;
    private List<Comment> commentList;
    private double ratingAverage;
    private int countAllReview;
    private int countAllLike;
    private int countFollow;
    private int countSupport;
    private int countProductLike;
    private boolean isLike;
}
