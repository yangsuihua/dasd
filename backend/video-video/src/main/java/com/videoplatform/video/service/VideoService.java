package com.videoplatform.video.service;

import com.videoplatform.video.dto.VideoDTO;
import com.videoplatform.video.dto.VideoPublishDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 视频服务接口
 */
public interface VideoService {
    
    /**
     * 获取视频推荐流
     */
    List<VideoDTO> getVideoFeed(Integer page, Integer size);
    
    /**
     * 获取视频详情
     */
    VideoDTO getVideoDetail(Long videoId);
    
    /**
     * 上传视频文件
     */
    String uploadVideoFile(MultipartFile file);
    
    /**
     * 上传封面文件
     */
    String uploadCoverFile(MultipartFile file);
    
    /**
     * 发布视频
     */
    Long publishVideo(Long userId, VideoPublishDTO dto);
    
    /**
     * 获取用户视频列表
     */
    List<VideoDTO> getUserVideos(Long userId, Integer page, Integer size);
    
    /**
     * 删除视频
     */
    void deleteVideo(Long userId, Long videoId);
    
    /**
     * 获取视频分类
     */
    List getCategories();
}