package com.example.campusanimal.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseVO {

    private String token;
    private UserInfoVO userInfo;
}
