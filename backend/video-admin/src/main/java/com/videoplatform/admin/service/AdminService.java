package com.videoplatform.admin.service;

import com.videoplatform.admin.dto.AdminStatsDTO;
import com.videoplatform.admin.mapper.UserMapper;
import com.videoplatform.admin.mapper.VideoMapper;
import com.videoplatform.common.entity.User;
import com.videoplatform.common.entity.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserMapper userMapper;
    private final VideoMapper videoMapper;
    
    /**
     * 获取系统统计信息
     */
    public AdminStatsDTO getSystemStats() {
        log.info("获取系统统计信息");
        
        AdminStatsDTO stats = new AdminStatsDTO();
        stats.setUserCount(userMapper.selectCount(null));
        stats.setVideoCount(videoMapper.selectCount(null));
        
        // TODO: 实现其他统计逻辑
        // stats.setCommentCount(commentMapper.selectCount(null));
        // stats.setLikeCount(likeMapper.selectCount(null));
        
        return stats;
    }
}