package com.example.campusanimal.controller;

import com.example.campusanimal.common.ApiResponse;
import com.example.campusanimal.common.PageResult;
import com.example.campusanimal.dto.AnimalCreateRequest;
import com.example.campusanimal.dto.AnimalUpdateRequest;
import com.example.campusanimal.service.AnimalService;
import com.example.campusanimal.service.AuthService;
import com.example.campusanimal.util.AuthUtil;
import com.example.campusanimal.vo.AnimalVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    private final AnimalService animalService;
    private final AuthService authService;

    public AnimalController(AnimalService animalService, AuthService authService) {
        this.animalService = animalService;
        this.authService = authService;
    }

    @GetMapping
    public ApiResponse<PageResult<AnimalVO>> list(@RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) String species,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(animalService.list(keyword, species, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<AnimalVO> detail(@PathVariable Long id) {
        return ApiResponse.success(animalService.detail(id));
    }

    @PostMapping
    public ApiResponse<AnimalVO> create(@RequestHeader(value = "Authorization", required = false) String authorization,
                                        @Valid @RequestBody AnimalCreateRequest request) {
        return ApiResponse.success(
                "建档成功",
                animalService.create(request, authService.requireUser(AuthUtil.extractBearerToken(authorization)))
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@RequestHeader(value = "Authorization", required = false) String authorization,
                                       @PathVariable Long id,
                                       @RequestBody AnimalUpdateRequest request) {
        return ApiResponse.success(
                "修改成功",
                animalService.update(id, request, authService.requireUser(AuthUtil.extractBearerToken(authorization)))
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@RequestHeader(value = "Authorization", required = false) String authorization,
                                       @PathVariable Long id) {
        return ApiResponse.success(
                "删除成功",
                animalService.delete(id, authService.requireUser(AuthUtil.extractBearerToken(authorization)))
        );
    }
}
