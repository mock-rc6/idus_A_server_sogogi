package com.example.demo.src.classes.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class GetOnlineClasses {
    private List<BestReview> bestReviewList;
    private List<NewOpenClasses> newOpenClassesList;
    private List<UserLikeClasses> userLikeClassesList;
    private List<AllOnlineClasses> allOnlineClassesList;
}
