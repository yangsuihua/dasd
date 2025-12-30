package com.videoplatform.search.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class SearchStatsService {
    
    // 搜索关键词统计
    private final ConcurrentHashMap<String, AtomicLong> searchKeywordStats = new ConcurrentHashMap<>();
    
    // 热门搜索缓存
    private volatile String[] hotSearches = new String[0];
    
    /**
     * 记录搜索关键词
     */
    public void recordSearchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        
        searchKeywordStats.computeIfAbsent(keyword.toLowerCase(), k -> new AtomicLong(0))
                .incrementAndGet();
        
        // 简单的热门搜索更新策略
        updateHotSearches();
    }
    
    /**
     * 更新热门搜索
     */
    private void updateHotSearches() {
        // 获取前10个最热门的搜索词
        String[] newHotSearches = searchKeywordStats.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue().get(), e1.getValue().get()))
                .limit(10)
                .map(entry -> entry.getKey())
                .toArray(String[]::new);
        
        this.hotSearches = newHotSearches;
    }
    
    /**
     * 获取热门搜索
     */
    public String[] getHotSearches() {
        return hotSearches;
    }
}