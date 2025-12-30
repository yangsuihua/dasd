package com.videoplatform.interaction.service;

/**
 * 点赞服务接口
 */
public interface LikeService {
    
    /**
     * 点赞视频
     */
    void likeVideo(Long userId, Long videoId);
    
    /**
     * 取消点赞
     */
    void unlikeVideo(Long userId, Long videoId);
    
    /**
     * 检查是否点赞
     */
    boolean isLiked(Long userId, Long videoId);
}