package com.videoplatform.interaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.videoplatform.common.entity.UserLike;
import com.videoplatform.common.entity.Video;
import com.videoplatform.interaction.mapper.UserLikeMapper;
import com.videoplatform.interaction.mapper.VideoMapper;
import com.videoplatform.interaction.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 点赞服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final UserLikeMapper userLikeMapper;
    private final VideoMapper videoMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeVideo(Long userId, Long videoId) {
        if (userId == null || videoId == null) {
            return;
        }
        UserLike existing = userLikeMapper.selectOne(new LambdaQueryWrapper<UserLike>()
                .eq(UserLike::getUserId, userId)
                .eq(UserLike::getVideoId, videoId));
        if (existing != null) {
            return;
        }
        UserLike like = new UserLike();
        like.setUserId(userId);
        like.setVideoId(videoId);
        like.setCreatedAt(LocalDateTime.now());
        userLikeMapper.insert(like);

        LambdaUpdateWrapper<Video> update = new LambdaUpdateWrapper<Video>()
                .eq(Video::getId, videoId)
                .setSql("like_count = like_count + 1");
        videoMapper.update(null, update);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikeVideo(Long userId, Long videoId) {
        if (userId == null || videoId == null) {
            return;
        }
        int removed = userLikeMapper.delete(new LambdaQueryWrapper<UserLike>()
                .eq(UserLike::getUserId, userId)
                .eq(UserLike::getVideoId, videoId));
        if (removed > 0) {
            LambdaUpdateWrapper<Video> update = new LambdaUpdateWrapper<Video>()
                    .eq(Video::getId, videoId)
                    .setSql("like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END");
            videoMapper.update(null, update);
        }
    }
    
    @Override
    public boolean isLiked(Long userId, Long videoId) {
        if (userId == null || videoId == null) {
            return false;
        }
        Long count = userLikeMapper.selectCount(new LambdaQueryWrapper<UserLike>()
                .eq(UserLike::getUserId, userId)
                .eq(UserLike::getVideoId, videoId));
        return count != null && count > 0;
    }
}
