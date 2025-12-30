package com.videoplatform.video.controller;

import com.videoplatform.common.result.Result;
import com.videoplatform.video.dto.VideoDTO;
import com.videoplatform.video.dto.VideoPublishDTO;
import com.videoplatform.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 视频控制器
 */
@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {
    
    private final VideoService videoService;
    
    /**
     * 获取视频推荐流
     */
    @GetMapping("/feed")
    public Result<List<VideoDTO>> getVideoFeed(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size) {
        List<VideoDTO> videos = videoService.getVideoFeed(page, size);
        return Result.success(videos);
    }
    
    /**
     * 获取视频详情
     */
    @GetMapping("/{videoId}")
    public Result<VideoDTO> getVideoDetail(@PathVariable Long videoId) {
        VideoDTO video = videoService.getVideoDetail(videoId);
        return Result.success(video);
    }
    
    /**
     * 上传视频文件
     */
    @PostMapping("/upload")
    public Result<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        String videoUrl = videoService.uploadVideoFile(file);
        return Result.success(videoUrl);
    }
    
    /**
     * 上传封面
     */
    @PostMapping("/upload/cover")
    public Result<String> uploadCover(@RequestParam("file") MultipartFile file) {
        String coverUrl = videoService.uploadCoverFile(file);
        return Result.success(coverUrl);
    }
    
    /**
     * 发布视频
     */
    @PostMapping("/publish")
    public Result<Long> publishVideo(@RequestHeader("X-User-Id") Long userId,
                                      @RequestBody VideoPublishDTO dto) {
        Long videoId = videoService.publishVideo(userId, dto);
        return Result.success(videoId);
    }
    
    /**
     * 获取用户视频列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<VideoDTO>> getUserVideos(@PathVariable Long userId,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        List<VideoDTO> videos = videoService.getUserVideos(userId, page, size);
        return Result.success(videos);
    }
    
    /**
     * 删除视频
     */
    @DeleteMapping("/{videoId}")
    public Result<Void> deleteVideo(@RequestHeader("X-User-Id") Long userId,
                                     @PathVariable Long videoId) {
        videoService.deleteVideo(userId, videoId);
        return Result.success();
    }
    
    /**
     * 获取视频分类
     */
    @GetMapping("/categories")
    public Result<List> getCategories() {
        return Result.success(videoService.getCategories());
    }
}
