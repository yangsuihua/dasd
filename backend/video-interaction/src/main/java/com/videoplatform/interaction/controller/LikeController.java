package com.videoplatform.interaction.controller;

import com.videoplatform.common.result.Result;
import com.videoplatform.interaction.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞控制器
 */
@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
    
    private final LikeService likeService;
    
    /**
     * 点赞视频
     */
    @PostMapping("/{videoId}")
    public Result<Void> likeVideo(@RequestHeader("X-User-Id") Long userId,
                                   @PathVariable Long videoId) {
        likeService.likeVideo(userId, videoId);
        return Result.success();
    }
    
    /**
     * 取消点赞
     */
    @DeleteMapping("/{videoId}")
    public Result<Void> unlikeVideo(@RequestHeader("X-User-Id") Long userId,
                                     @PathVariable Long videoId) {
        likeService.unlikeVideo(userId, videoId);
        return Result.success();
    }
    
    /**
     * 检查是否点赞
     */
    @GetMapping("/check/{videoId}")
    public Result<Boolean> checkLiked(@RequestHeader("X-User-Id") Long userId,
                                       @PathVariable Long videoId) {
        boolean isLiked = likeService.isLiked(userId, videoId);
        return Result.success(isLiked);
    }
}
