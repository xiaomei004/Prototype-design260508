package com.example.campusanimal.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AnimalVO {

    private Long id;
    private String name;
    private String species;
    private String gender;
    private String color;
    private String location;
    private String status;
    private String features;
    private String imageUrl;
    private Integer sterilized;
    private String vaccineStatus;
    private LocalDateTime firstFoundTime;
    private String auditStatus;
    private Long createUserId;
    private LocalDateTime createTime;
}
