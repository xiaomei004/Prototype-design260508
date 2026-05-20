package com.example.campusanimal.controller;

import com.example.campusanimal.common.ApiResponse;
import com.example.campusanimal.common.PageResult;
import com.example.campusanimal.dto.PostCreateRequest;
import com.example.campusanimal.dto.PostUpdateRequest;
import com.example.campusanimal.service.AuthService;
import com.example.campusanimal.service.PostService;
import com.example.campusanimal.util.AuthUtil;
import com.example.campusanimal.vo.PostVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final AuthService authService;

    public PostController(PostService postService, AuthService authService) {
        this.postService = postService;
        this.authService = authService;
    }

    @GetMapping
    public ApiResponse<PageResult<PostVO>> list(@RequestParam(required = false) String type,
                                                @RequestParam(required = false) Long animalId,
                                                @RequestParam(defaultValue = "1") int pageNum,
                                                @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(postService.list(type, animalId, pageNum, pageSize));
    }

    @PostMapping
    public ApiResponse<PostVO> create(@RequestHeader(value = "Authorization", required = false) String authorization,
                                      @RequestBody PostCreateRequest request) {
        return ApiResponse.success(
                "发布成功",
                postService.create(request, authService.requireUser(AuthUtil.extractBearerToken(authorization)))
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<PostVO> update(@RequestHeader(value = "Authorization", required = false) String authorization,
                                      @PathVariable Long id,
                                      @RequestBody PostUpdateRequest request) {
        return ApiResponse.success(
                "更新成功",
                postService.update(id, request, authService.requireUser(AuthUtil.extractBearerToken(authorization)))
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@RequestHeader(value = "Authorization", required = false) String authorization,
                                       @PathVariable Long id) {
        return ApiResponse.success(
                "删除成功",
                postService.delete(id, authService.requireUser(AuthUtil.extractBearerToken(authorization)))
        );
    }
}
