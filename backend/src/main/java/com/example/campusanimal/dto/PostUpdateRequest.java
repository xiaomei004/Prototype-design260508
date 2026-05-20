package com.example.campusanimal.dto;

import lombok.Data;

@Data
public class PostUpdateRequest {

    private Long animalId;
    private String type;
    private String content;
    private String imageUrl;
    private String location;
}
