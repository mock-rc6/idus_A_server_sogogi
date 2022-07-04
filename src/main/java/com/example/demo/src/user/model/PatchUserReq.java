package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PatchUserReq {
    private String profileImg;
    private String userName;
    private String email;
    private String birthDay;
    private Character gender;
    private String phoneNumber;
    private String addressName;
}
