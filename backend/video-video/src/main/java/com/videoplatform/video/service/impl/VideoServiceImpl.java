package com.videoplatform.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.videoplatform.common.entity.User;
import com.videoplatform.common.entity.Video;
import com.videoplatform.common.entity.VideoCategory;
import com.videoplatform.common.entity.VideoTag;
import com.videoplatform.common.entity.VideoTagRelation;
import com.videoplatform.video.dto.VideoDTO;
import com.videoplatform.video.dto.VideoPublishDTO;
import com.videoplatform.video.mapper.UserMapper;
import com.videoplatform.video.mapper.VideoCategoryMapper;
import com.videoplatform.video.mapper.VideoMapper;
import com.videoplatform.video.mapper.VideoTagMapper;
import com.videoplatform.video.mapper.VideoTagRelationMapper;
import com.videoplatform.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 视频服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final VideoMapper videoMapper;
    private final VideoCategoryMapper videoCategoryMapper;
    private final VideoTagMapper videoTagMapper;
    private final VideoTagRelationMapper videoTagRelationMapper;
    private final UserMapper userMapper;

    @Override
    public List<VideoDTO> getVideoFeed(Integer page, Integer size) {
        int current = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : size;

        Page<Video> pager = new Page<>(current, pageSize);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<Video>()
                .eq(Video::getStatus, 1)
                .eq(Video::getDeleted, 0)
                .orderByDesc(Video::getPublishedAt)
                .orderByDesc(Video::getViewCount)
                .orderByDesc(Video::getLikeCount);
        videoMapper.selectPage(pager, wrapper);
        List<Video> videos = pager.getRecords();
        return convertToDTOs(videos);
    }

    @Override
    public VideoDTO getVideoDetail(Long videoId) {
        if (videoId == null) {
            return null;
        }
        Video video = videoMapper.selectById(videoId);
        if (video == null || (video.getDeleted() != null && video.getDeleted() == 1) || (video.getStatus() != null && video.getStatus() != 1)) {
            return null;
        }
        List<VideoDTO> list = convertToDTOs(Collections.singletonList(video));
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public String uploadVideoFile(MultipartFile file) {
        return storeFile(file, "videos");
    }

    @Override
    public String uploadCoverFile(MultipartFile file) {
        return storeFile(file, "covers");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishVideo(Long userId, VideoPublishDTO dto) {
        if (userId == null || dto == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        Video video = new Video();
        video.setUserId(userId);
        video.setTitle(dto.getTitle());
        video.setDescription(dto.getDescription());
        video.setCoverUrl(dto.getCoverUrl());
        video.setVideoUrl(dto.getVideoUrl());
        video.setCategoryId(dto.getCategoryId());
        video.setStatus(1);
        video.setIsPrivate(dto.getIsPrivate() == null ? 0 : dto.getIsPrivate());
        video.setViewCount(0L);
        video.setLikeCount(0);
        video.setCommentCount(0);
        video.setShareCount(0);
        video.setFavoriteCount(0);
        video.setPublishedAt(now);
        video.setCreatedAt(now);
        video.setUpdatedAt(now);
        videoMapper.insert(video);

        handleTags(dto.getTags(), video.getId());
        return video.getId();
    }

    @Override
    public List<VideoDTO> getUserVideos(Long userId, Integer page, Integer size) {
        if (userId == null) {
            return Collections.emptyList();
        }
        int current = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : size;

        Page<Video> pager = new Page<>(current, pageSize);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<Video>()
                .eq(Video::getUserId, userId)
                .eq(Video::getDeleted, 0)
                .orderByDesc(Video::getCreatedAt);
        videoMapper.selectPage(pager, wrapper);
        return convertToDTOs(pager.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideo(Long userId, Long videoId) {
        if (userId == null || videoId == null) {
            return;
        }
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            return;
        }
        if (!userId.equals(video.getUserId())) {
            throw new IllegalArgumentException("无权限删除该视频");
        }
        LambdaUpdateWrapper<Video> update = new LambdaUpdateWrapper<Video>()
                .eq(Video::getId, videoId)
                .set(Video::getDeleted, 1)
                .set(Video::getStatus, 0)
                .set(Video::getUpdatedAt, LocalDateTime.now());
        videoMapper.update(null, update);
    }

    @Override
    public List<VideoCategory> getCategories() {
        LambdaQueryWrapper<VideoCategory> wrapper = new LambdaQueryWrapper<VideoCategory>()
                .eq(VideoCategory::getStatus, 1)
                .orderByAsc(VideoCategory::getSort)
                .orderByAsc(VideoCategory::getId);
        return videoCategoryMapper.selectList(wrapper);
    }

    private List<VideoDTO> convertToDTOs(List<Video> videos) {
        if (videos == null || videos.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> userIds = new HashSet<Long>();
        Set<Integer> categoryIds = new HashSet<Integer>();
        for (Video video : videos) {
            if (video.getUserId() != null) {
                userIds.add(video.getUserId());
            }
            if (video.getCategoryId() != null) {
                categoryIds.add(video.getCategoryId());
            }
        }

        Map<Long, User> userMap = loadUsers(userIds);
        Map<Integer, VideoCategory> categoryMap = loadCategories(categoryIds);

        List<VideoDTO> result = new ArrayList<VideoDTO>(videos.size());
        for (Video video : videos) {
            VideoDTO dto = new VideoDTO();
            dto.setId(video.getId());
            dto.setUserId(video.getUserId());

            User user = userMap.get(video.getUserId());
            if (user != null) {
                dto.setUsername(user.getUsername());
                dto.setAvatar(user.getAvatar());
            }

            dto.setTitle(video.getTitle());
            dto.setDescription(video.getDescription());
            dto.setCoverUrl(video.getCoverUrl());
            dto.setVideoUrl(video.getVideoUrl());
            dto.setDuration(video.getDuration());
            VideoCategory category = categoryMap.get(video.getCategoryId());
            if (category != null) {
                dto.setCategory(category.getName());
            }
            dto.setViewCount(video.getViewCount());
            dto.setLikeCount(video.getLikeCount());
            dto.setCommentCount(video.getCommentCount());
            dto.setShareCount(video.getShareCount());
            dto.setFavoriteCount(video.getFavoriteCount());
            LocalDateTime time = video.getPublishedAt() != null ? video.getPublishedAt() : video.getCreatedAt();
            dto.setCreatedAt(time == null ? null : DATE_TIME_FORMATTER.format(time));

            dto.setIsLiked(Boolean.FALSE);
            dto.setIsFavorited(Boolean.FALSE);
            dto.setIsFollowing(Boolean.FALSE);
            result.add(dto);
        }
        return result;
    }

    private Map<Long, User> loadUsers(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<User> users = userMapper.selectBatchIds(new ArrayList<Long>(userIds));
        Map<Long, User> map = new HashMap<Long, User>();
        for (User user : users) {
            map.put(user.getId(), user);
        }
        return map;
    }

    private Map<Integer, VideoCategory> loadCategories(Set<Integer> categoryIds) {
        if (categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<VideoCategory> wrapper = new LambdaQueryWrapper<VideoCategory>()
                .in(VideoCategory::getId, categoryIds);
        List<VideoCategory> categories = videoCategoryMapper.selectList(wrapper);
        Map<Integer, VideoCategory> map = new HashMap<Integer, VideoCategory>();
        for (VideoCategory category : categories) {
            map.put(category.getId(), category);
        }
        return map;
    }

    private String storeFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (StringUtils.hasText(originalName) && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;
        Path dir = Paths.get("uploads").resolve(folder);
        try {
            Files.createDirectories(dir);
            Path dest = dir.resolve(filename);
            file.transferTo(dest.toFile());
            return "/uploads/" + folder + "/" + filename;
        } catch (IOException e) {
            log.error("保存文件失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    private void handleTags(String tags, Long videoId) {
        if (!StringUtils.hasText(tags) || videoId == null) {
            return;
        }
        String[] tagArr = tags.split(",");
        LocalDateTime now = LocalDateTime.now();
        for (String tagName : tagArr) {
            String name = tagName.trim();
            if (!StringUtils.hasText(name)) {
                continue;
            }
            VideoTag tag = videoTagMapper.selectOne(new LambdaQueryWrapper<VideoTag>().eq(VideoTag::getName, name));
            if (tag == null) {
                tag = new VideoTag();
                tag.setName(name);
                tag.setVideoCount(1);
                tag.setCreatedAt(now);
                videoTagMapper.insert(tag);
            } else {
                LambdaUpdateWrapper<VideoTag> update = new LambdaUpdateWrapper<VideoTag>()
                        .eq(VideoTag::getId, tag.getId())
                        .setSql("video_count = video_count + 1");
                videoTagMapper.update(null, update);
            }
            VideoTagRelation relation = new VideoTagRelation();
            relation.setVideoId(videoId);
            relation.setTagId(tag.getId());
            relation.setCreatedAt(now);
            videoTagRelationMapper.insert(relation);
        }
    }
}
