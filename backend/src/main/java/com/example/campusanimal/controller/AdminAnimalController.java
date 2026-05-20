package com.example.campusanimal.controller;

import com.example.campusanimal.common.ApiResponse;
import com.example.campusanimal.dto.AuditRequest;
import com.example.campusanimal.service.AnimalService;
import com.example.campusanimal.service.AuthService;
import com.example.campusanimal.util.AuthUtil;
import com.example.campusanimal.vo.AnimalVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/animals")
public class AdminAnimalController {

    private final AnimalService animalService;
    private final AuthService authService;

    public AdminAnimalController(AnimalService animalService, AuthService authService) {
        this.animalService = animalService;
        this.authService = authService;
    }

    @PutMapping("/{id}/audit")
    public ApiResponse<Boolean> audit(@RequestHeader(value = "Authorization", required = false) String authorization,
                                      @PathVariable Long id,
                                      @Valid @RequestBody AuditRequest request) {
        authService.requireAdmin(AuthUtil.extractBearerToken(authorization));
        return ApiResponse.success("审核成功", animalService.audit(id, request.getAuditStatus()));
    }

    @GetMapping("/pending")
    public ApiResponse<List<AnimalVO>> pending(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireAdmin(AuthUtil.extractBearerToken(authorization));
        return ApiResponse.success(animalService.pendingList());
    }
}
