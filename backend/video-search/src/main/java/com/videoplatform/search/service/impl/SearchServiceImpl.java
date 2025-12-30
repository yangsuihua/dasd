package com.videoplatform.search.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.videoplatform.common.entity.User;
import com.videoplatform.common.entity.Video;
import com.videoplatform.search.dto.SearchResultDTO;
import com.videoplatform.search.mapper.UserMapper;
import com.videoplatform.search.mapper.VideoMapper;
import com.videoplatform.search.service.ElasticsearchSearchService;
import com.videoplatform.search.service.SearchService;
import com.videoplatform.search.service.SearchStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    
    private final VideoMapper videoMapper;
    private final UserMapper userMapper;
    private final ElasticsearchSearchService elasticsearchSearchService;
    private final SearchStatsService searchStatsService;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<SearchResultDTO> searchVideos(String keyword, Integer page, Integer size) {
        log.info("搜索视频: keyword={}, page={}, size={}", keyword, page, size);
        
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }
        
        // 记录搜索关键词
        searchStatsService.recordSearchKeyword(keyword);
        
        // 优先使用 Elasticsearch 搜索，如果失败则回退到数据库搜索
        try {
            List<SearchResultDTO> esResults = elasticsearchSearchService.searchVideosInElasticsearch(keyword, page, size);
            if (esResults != null && !esResults.isEmpty()) {
                return esResults;
            }
        } catch (Exception e) {
            log.warn("Elasticsearch 搜索视频失败，回退到数据库搜索", e);
        }
        
        // 回退到数据库搜索
        return searchVideosInDatabase(keyword, page, size);
    }
    
    private List<SearchResultDTO> searchVideosInDatabase(String keyword, Integer page, Integer size) {
        int current = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : Math.min(size, 100);
        
        // 使用 mapper 进行搜索
        List<Video> videos = videoMapper.searchVideos(keyword);
        
        // 分页处理
        int fromIndex = (current - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, videos.size());
        
        if (fromIndex >= videos.size()) {
            return new ArrayList<>();
        }
        
        List<Video> pagedVideos = videos.subList(fromIndex, toIndex);
        return pagedVideos.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<SearchResultDTO> searchUsers(String keyword, Integer page, Integer size) {
        log.info("搜索用户: keyword={}, page={}, size={}", keyword, page, size);
        
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }
        
        // 记录搜索关键词
        searchStatsService.recordSearchKeyword(keyword);
        
        // 优先使用 Elasticsearch 搜索，如果失败则回退到数据库搜索
        try {
            List<SearchResultDTO> esResults = elasticsearchSearchService.searchUsersInElasticsearch(keyword, page, size);
            if (esResults != null && !esResults.isEmpty()) {
                return esResults;
            }
        } catch (Exception e) {
            log.warn("Elasticsearch 搜索用户失败，回退到数据库搜索", e);
        }
        
        // 回退到数据库搜索
        return searchUsersInDatabase(keyword, page, size);
    }
    
    private List<SearchResultDTO> searchUsersInDatabase(String keyword, Integer page, Integer size) {
        int current = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : Math.min(size, 100);
        
        // 使用 mapper 进行搜索
        List<User> users = userMapper.searchUsers(keyword);
        
        // 分页处理
        int fromIndex = (current - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, users.size());
        
        if (fromIndex >= users.size()) {
            return new ArrayList<>();
        }
        
        List<User> pagedUsers = users.subList(fromIndex, toIndex);
        return pagedUsers.stream().map(this::convertUserToDTO).collect(Collectors.toList());
    }

    @Override
    public List<String> getHotSearches() {
        log.info("获取热搜榜");
        try {
            // 优先使用统计服务的热门搜索
            String[] statsHotSearches = searchStatsService.getHotSearches();
            if (statsHotSearches.length > 0) {
                return List.of(statsHotSearches);
            }
            
            // 回退到数据库热门标签
            return videoMapper.getHotSearchKeywords();
        } catch (Exception e) {
            log.error("获取热搜榜失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getSuggestions(String keyword) {
        log.info("获取搜索建议: keyword={}", keyword);
        
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }
        
        try {
            // 简单实现：从热搜榜中筛选包含关键词的项
            List<String> hotSearches = getHotSearches();
            return hotSearches.stream()
                    .filter(tag -> tag.contains(keyword))
                    .limit(10)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取搜索建议失败", e);
            return new ArrayList<>();
        }
    }
    
    private SearchResultDTO convertToDTO(Video video) {
        SearchResultDTO dto = new SearchResultDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setDescription(video.getDescription());
        dto.setCoverUrl(video.getCoverUrl());
        dto.setUserId(video.getUserId());
        dto.setViewCount(video.getViewCount() != null ? video.getViewCount().intValue() : 0);
        dto.setLikeCount(video.getLikeCount());
        dto.setCommentCount(video.getCommentCount());
        dto.setType("video");
        if (video.getPublishedAt() != null) {
            dto.setCreatedAt(video.getPublishedAt().format(DATE_TIME_FORMATTER));
        }
        return dto;
    }
    
    private SearchResultDTO convertUserToDTO(User user) {
        SearchResultDTO dto = new SearchResultDTO();
        dto.setId(user.getId());
        dto.setTitle(user.getNickname());
        dto.setDescription(user.getBio());
        dto.setAvatar(user.getAvatar());
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setViewCount(user.getFollowerCount());
        dto.setLikeCount(user.getLikeCount() != null ? user.getLikeCount().intValue() : 0);
        dto.setCommentCount(user.getVideoCount());
        dto.setType("user");
        if (user.getCreatedAt() != null) {
            dto.setCreatedAt(user.getCreatedAt().format(DATE_TIME_FORMATTER));
        }
        return dto;
    }
}