package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GetUserDetail {
    private String profileImg;
    private String userName;
    private String email;
    private String birthDay;
    private String gender;
    private String phoneNumber;
    private String addressName;
}
