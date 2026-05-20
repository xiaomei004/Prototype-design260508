package com.example.campusanimal.controller;

import com.example.campusanimal.common.ApiResponse;
import com.example.campusanimal.dto.ScanRequest;
import com.example.campusanimal.service.AuthService;
import com.example.campusanimal.service.ScanService;
import com.example.campusanimal.util.AuthUtil;
import com.example.campusanimal.vo.ScanResultVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scan")
public class ScanController {

    private final ScanService scanService;
    private final AuthService authService;

    public ScanController(ScanService scanService, AuthService authService) {
        this.scanService = scanService;
        this.authService = authService;
    }

    @PostMapping
    public ApiResponse<ScanResultVO> scan(@RequestHeader(value = "Authorization", required = false) String authorization,
                                          @Valid @RequestBody ScanRequest request) {
        ScanResultVO result = scanService.scan(request, authService.requireUser(AuthUtil.extractBearerToken(authorization)));
        String message = "matched".equals(result.getResultType()) ? "识别成功" : "发现新成员";
        return ApiResponse.success(message, result);
    }
}
