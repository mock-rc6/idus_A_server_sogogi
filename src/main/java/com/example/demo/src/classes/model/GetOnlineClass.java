package com.example.demo.src.classes.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class GetOnlineClass {

    public GetOnlineClass(String videoUrl, long writerId, String writerImg, String writerName, String categoryName,
                          String title, boolean streaming, String startingDay, String level, boolean userLike,
                          int countLike, double rating, int countReview, String contents) {
        this.videoUrl = videoUrl;
        this.writerId = writerId;
        this.writerImg = writerImg;
        this.writerName = writerName;
        this.categoryName = categoryName;
        this.title = title;
        this.streaming = streaming;
        this.startingDay = startingDay;
        this.level = level;
        this.userLike = userLike;
        this.countLike = countLike;
        this.rating = rating;
        this.countReview = countReview;
        this.contents = contents;
    }

    private String videoUrl;
    private long writerId;
    private String writerImg;
    private String writerName;
    private String categoryName;
    private String title;
    private boolean streaming;
    private String startingDay;
    private String level;
    private boolean userLike;
    private int countLike;
    private double rating;
    private int countReview;
    private List<OnlineClassReview> onlineClassReviewList;
    private String contents;
    private List<ClassComment> onlineClassCommentList;
}
