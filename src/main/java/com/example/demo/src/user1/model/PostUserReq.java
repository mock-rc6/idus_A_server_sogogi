package com.example.demo.src.user1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    private String UserName;
    private String id;
    private String email;
    private String password;
}
