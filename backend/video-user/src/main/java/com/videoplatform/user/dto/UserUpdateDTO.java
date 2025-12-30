package com.videoplatform.user.dto;

import lombok.Data;

/**
 * 用户更新DTO
 */
@Data
public class UserUpdateDTO {
    
    private String nickname;
    
    private String bio;
    
    private Integer gender;
    
    private String birthday;
}
