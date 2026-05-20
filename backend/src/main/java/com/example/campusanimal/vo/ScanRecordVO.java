package com.example.campusanimal.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ScanRecordVO {

    private Long id;
    private String imageUrl;
    private String resultType;
    private Long matchedAnimalId;
    private String matchedAnimalName;
    private Double similarity;
    private LocalDateTime createTime;
}
