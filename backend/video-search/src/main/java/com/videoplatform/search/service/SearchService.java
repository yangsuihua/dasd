package com.videoplatform.search.service;

import com.videoplatform.search.dto.SearchResultDTO;

import java.util.List;

public interface SearchService {

    /**
     * 搜索视频
     */
    List<SearchResultDTO> searchVideos(String keyword, Integer page, Integer size);

    /**
     * 搜索用户
     */
    List<SearchResultDTO> searchUsers(String keyword, Integer page, Integer size);

    /**
     * 获取热搜榜
     */
    List<String> getHotSearches();

    /**
     * 获取搜索建议
     */
    List<String> getSuggestions(String keyword);
}