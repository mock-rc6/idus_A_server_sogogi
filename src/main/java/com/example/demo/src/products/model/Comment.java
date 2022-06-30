package com.example.demo.src.products.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Comment {
    private String nickName;
    private String profileImg;
    private String contents;
}
