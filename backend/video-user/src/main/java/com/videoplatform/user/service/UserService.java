package com.videoplatform.user.service;

import com.videoplatform.user.dto.UserProfileVO;
import com.videoplatform.user.dto.UserUpdateDTO;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 获取用户资料
     */
    UserProfileVO getUserProfile(Long userId);
    
    /**
     * 更新用户信息
     */
    void updateUser(Long userId, UserUpdateDTO dto);
    
    /**
     * 更新头像
     */
    void updateAvatar(Long userId, String avatarUrl);
}
