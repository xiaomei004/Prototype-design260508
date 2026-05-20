package com.example.campusanimal.controller;

import com.example.campusanimal.common.ApiResponse;
import com.example.campusanimal.service.AuthService;
import com.example.campusanimal.service.FileStorageService;
import com.example.campusanimal.util.AuthUtil;
import com.example.campusanimal.vo.UploadVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final FileStorageService fileStorageService;
    private final AuthService authService;

    public UploadController(FileStorageService fileStorageService, AuthService authService) {
        this.fileStorageService = fileStorageService;
        this.authService = authService;
    }

    @PostMapping
    public ApiResponse<UploadVO> upload(@RequestHeader(value = "Authorization", required = false) String authorization,
                                        @RequestParam("file") MultipartFile file) throws IOException {
        authService.requireUser(AuthUtil.extractBearerToken(authorization));
        return ApiResponse.success("上传成功", fileStorageService.store(file));
    }
}
