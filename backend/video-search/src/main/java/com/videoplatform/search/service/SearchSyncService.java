package com.videoplatform.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.videoplatform.common.entity.Video;
import com.videoplatform.search.dto.SearchResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchSyncService {
    
    private final ElasticsearchClient elasticsearchClient;
    
    /**
     * 创建 Elasticsearch 索引
     */
    public void createIndices() {
        try {
            // 创建视频索引
            CreateIndexRequest videoIndexRequest = CreateIndexRequest.of(c -> c
                .index("videos")
            );
            CreateIndexResponse videoIndexResponse = elasticsearchClient.indices().create(videoIndexRequest);
            log.info("创建视频索引结果: {}", videoIndexResponse.acknowledged());
            
            // 创建用户索引
            CreateIndexRequest userIndexRequest = CreateIndexRequest.of(c -> c
                .index("users")
            );
            CreateIndexResponse userIndexResponse = elasticsearchClient.indices().create(userIndexRequest);
            log.info("创建用户索引结果: {}", userIndexResponse.acknowledged());
        } catch (IOException e) {
            log.error("创建 Elasticsearch 索引失败", e);
        }
    }
    
    /**
     * 同步视频到 Elasticsearch
     */
    public void syncVideoToElasticsearch(Video video) {
        try {
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
            
            IndexRequest<SearchResultDTO> request = IndexRequest.of(i -> i
                .index("videos")
                .id(String.valueOf(video.getId()))
                .document(dto)
            );
            
            IndexResponse response = elasticsearchClient.index(request);
            log.info("同步视频到 Elasticsearch 结果: {}", response.result());
        } catch (IOException e) {
            log.error("同步视频到 Elasticsearch 失败: videoId={}", video.getId(), e);
        }
    }
}