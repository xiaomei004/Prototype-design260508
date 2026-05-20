package com.example.campusanimal.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoVO {

    private Long id;
    private String username;
    private String nickname;
    private String role;
    private String avatar;
}
