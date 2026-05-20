package com.example.campusanimal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScanRequest {

    @NotBlank
    private String imageUrl;
}
