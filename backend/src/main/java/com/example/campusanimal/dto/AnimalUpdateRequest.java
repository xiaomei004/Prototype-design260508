package com.example.campusanimal.dto;

import lombok.Data;

@Data
public class AnimalUpdateRequest {

    private String location;
    private String status;
    private String features;
    private String auditStatus;
}
