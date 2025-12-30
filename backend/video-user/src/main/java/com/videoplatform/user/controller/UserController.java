package com.videoplatform.user.controller;

import com.videoplatform.common.result.Result;
import com.videoplatform.user.dto.UserProfileVO;
import com.videoplatform.user.dto.UserUpdateDTO;
import com.videoplatform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取用户信息
     */
    @GetMapping("/profile/{userId}")
    public Result<UserProfileVO> getUserProfile(@PathVariable Long userId) {
        UserProfileVO profile = userService.getUserProfile(userId);
        return Result.success(profile);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<UserProfileVO> getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        UserProfileVO profile = userService.getUserProfile(userId);
        return Result.success(profile);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<Void> updateUser(@RequestHeader("X-User-Id") Long userId, 
                                    @RequestBody UserUpdateDTO dto) {
        userService.updateUser(userId, dto);
        return Result.success();
    }
    
    /**
     * 更新头像
     */
    @PostMapping("/avatar")
    public Result<String> updateAvatar(@RequestHeader("X-User-Id") Long userId,
                                        @RequestParam String avatarUrl) {
        userService.updateAvatar(userId, avatarUrl);
        return Result.success(avatarUrl);
    }
}
