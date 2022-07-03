package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GetUserInfo {
    private String profileImg;
    private String grade;
    private String userName;
    private int rewardPoint;
    private int countCoupon;
}
