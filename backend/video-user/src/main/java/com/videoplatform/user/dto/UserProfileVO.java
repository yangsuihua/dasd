package com.videoplatform.user.dto;

import lombok.Data;

/**
 * 用户资料VO
 */
@Data
public class UserProfileVO {
    
    private Long id;
    
    private String username;
    
    private String nickname;
    
    private String avatar;
    
    private String bio;
    
    private Integer gender;
    
    private Integer followerCount;
    
    private Integer followingCount;
    
    private Long likeCount;
    
    private Integer videoCount;
    
    /**
     * 是否已关注(当前用户视角)
     */
    private Boolean isFollowing;
}
