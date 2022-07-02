package com.example.demo.src.classes.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class GetOfflineClasses {
    private List<ClassCategory> classCategoryList;
    private String userAddressName;
    private List<NearOfflineClass> nearOfflineClassList;
    private List<NewOfflineClass> newOpenClassesList;
}
