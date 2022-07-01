package com.example.demo.src.classes.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class NewOpenClasses {
    private long classId;
    private String profileImg;
    private boolean isStreaming;
    private boolean isLike;
    private String categoryName;
    private String writerName;
    private String title;
}
