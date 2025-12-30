package com.videoplatform.search.config;

import com.videoplatform.search.service.SearchSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchInitConfig implements CommandLineRunner {
    
    private final SearchSyncService searchSyncService;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("初始化 Elasticsearch 索引...");
        try {
            searchSyncService.createIndices();
            log.info("Elasticsearch 索引初始化完成");
        } catch (Exception e) {
            log.error("Elasticsearch 索引初始化失败", e);
        }
    }
}