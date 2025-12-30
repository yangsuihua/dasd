package com.videoplatform.search.controller;

import com.videoplatform.common.result.Result;
import com.videoplatform.search.dto.SearchResultDTO;
import com.videoplatform.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 搜索视频
     */
    @GetMapping("/video")
    public Result<List<SearchResultDTO>> searchVideos(@RequestParam String keyword,
                                                      @RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        List<SearchResultDTO> results = searchService.searchVideos(keyword, page, size);
        return Result.success(results);
    }

    /**
     * 搜索用户
     */
    @GetMapping("/user")
    public Result<List<SearchResultDTO>> searchUsers(@RequestParam String keyword,
                                                     @RequestParam(defaultValue = "1") Integer page,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        List<SearchResultDTO> results = searchService.searchUsers(keyword, page, size);
        return Result.success(results);
    }

    /**
     * 热搜榜
     */
    @GetMapping("/hot")
    public Result<List<String>> getHotSearches() {
        List<String> hotSearches = searchService.getHotSearches();
        return Result.success(hotSearches);
    }

    /**
     * 搜索建议
     */
    @GetMapping("/suggest")
    public Result<List<String>> getSuggestions(@RequestParam String keyword) {
        List<String> suggestions = searchService.getSuggestions(keyword);
        return Result.success(suggestions);
    }
}