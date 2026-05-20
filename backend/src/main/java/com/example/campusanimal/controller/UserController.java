package com.example.campusanimal.controller;

import com.example.campusanimal.common.ApiResponse;
import com.example.campusanimal.service.AnimalService;
import com.example.campusanimal.service.AuthService;
import com.example.campusanimal.service.PostService;
import com.example.campusanimal.service.ScanRecordService;
import com.example.campusanimal.util.AuthUtil;
import com.example.campusanimal.vo.AnimalVO;
import com.example.campusanimal.vo.PostVO;
import com.example.campusanimal.vo.ScanRecordVO;
import com.example.campusanimal.vo.UserInfoVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AuthService authService;
    private final AnimalService animalService;
    private final PostService postService;
    private final ScanRecordService scanRecordService;

    public UserController(AuthService authService,
                          AnimalService animalService,
                          PostService postService,
                          ScanRecordService scanRecordService) {
        this.authService = authService;
        this.animalService = animalService;
        this.postService = postService;
        this.scanRecordService = scanRecordService;
    }

    @GetMapping("/me")
    public ApiResponse<UserInfoVO> currentUser(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ApiResponse.success(authService.toUserInfo(authService.requireUser(AuthUtil.extractBearerToken(authorization))));
    }

    @GetMapping("/animals")
    public ApiResponse<List<AnimalVO>> myAnimals(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = authService.requireUser(AuthUtil.extractBearerToken(authorization)).getId();
        return ApiResponse.success(animalService.findByUserId(userId));
    }

    @GetMapping("/posts")
    public ApiResponse<List<PostVO>> myPosts(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = authService.requireUser(AuthUtil.extractBearerToken(authorization)).getId();
        return ApiResponse.success(postService.findByUserId(userId));
    }

    @GetMapping("/scans")
    public ApiResponse<List<ScanRecordVO>> myScans(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = authService.requireUser(AuthUtil.extractBearerToken(authorization)).getId();
        return ApiResponse.success(scanRecordService.findByUserId(userId));
    }
}
