package com.example.campusanimal.service;

import com.example.campusanimal.dto.LoginRequest;
import com.example.campusanimal.dto.RegisterRequest;
import com.example.campusanimal.entity.User;
import com.example.campusanimal.exception.BusinessException;
import com.example.campusanimal.repository.UserRepository;
import com.example.campusanimal.vo.LoginResponseVO;
import com.example.campusanimal.vo.UserInfoVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenStore tokenStore;

    public AuthService(UserRepository userRepository, TokenStore tokenStore) {
        this.userRepository = userRepository;
        this.tokenStore = tokenStore;
    }

    public UserInfoVO register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException(400, "用户名已存在");
        }

        User user = userRepository.save(User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .role("USER")
                .avatar(null)
                .createTime(LocalDateTime.now())
                .build());
        return toUserInfo(user);
    }

    public LoginResponseVO login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .filter(item -> item.getPassword().equals(request.getPassword()))
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));

        String token = UUID.randomUUID().toString().replace("-", "");
        tokenStore.put(token, user.getId());
        return LoginResponseVO.builder()
                .token(token)
                .userInfo(toUserInfo(user))
                .build();
    }

    public User requireUser(String token) {
        Long userId = tokenStore.getUserId(token);
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(401, "用户不存在"));
    }

    public User requireAdmin(String token) {
        User user = requireUser(token);
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new BusinessException(403, "只有管理员才能执行该操作");
        }
        return user;
    }

    public UserInfoVO toUserInfo(User user) {
        return UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();
    }
}
