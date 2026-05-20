package com.example.campusanimal.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostVO {

    private Long id;
    private Long userId;
    private String nickname;
    private Long animalId;
    private String animalName;
    private String type;
    private String content;
    private String imageUrl;
    private String location;
    private LocalDateTime createTime;
}
