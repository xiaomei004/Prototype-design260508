package com.example.campusanimal.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnimalCreateRequest {

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
}
