package com.example.campusanimal.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScanResultVO {

    private String resultType;
    private Double similarity;
    private AnimalVO animal;
}
