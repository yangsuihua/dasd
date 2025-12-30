package com.videoplatform.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.videoplatform.search.dto.SearchResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchSearchService {
    
    private final ElasticsearchClient elasticsearchClient;
    
    public List<SearchResultDTO> searchVideosInElasticsearch(String keyword, Integer page, Integer size) {
        try {
            int current = page == null || page < 1 ? 1 : page;
            int pageSize = size == null || size < 1 ? 10 : Math.min(size, 100);
            int from = (current - 1) * pageSize;
            
            // 构建搜索请求
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("videos")
                .query(q -> q
                    .multiMatch(m -> m
                        .query(keyword)
                        .fields("title", "description", "tags")
                    )
                )
                .from(from)
                .size(pageSize)
            );
            
            // 执行搜索
            SearchResponse<SearchResultDTO> searchResponse = elasticsearchClient.search(searchRequest, SearchResultDTO.class);
            
            // 处理结果
            List<SearchResultDTO> results = new ArrayList<>();
            for (Hit<SearchResultDTO> hit : searchResponse.hits().hits()) {
                SearchResultDTO dto = hit.source();
                if (dto != null) {
                    dto.setId(hit.id() != null ? Long.valueOf(hit.id()) : null);
                    results.add(dto);
                }
            }
            
            return results;
        } catch (IOException e) {
            log.error("Elasticsearch 搜索视频失败: keyword={}", keyword, e);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Elasticsearch 搜索视频失败: keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }
    
    public List<SearchResultDTO> searchUsersInElasticsearch(String keyword, Integer page, Integer size) {
        try {
            int current = page == null || page < 1 ? 1 : page;
            int pageSize = size == null || size < 1 ? 10 : Math.min(size, 100);
            int from = (current - 1) * pageSize;
            
            // 构建搜索请求
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("users")
                .query(q -> q
                    .multiMatch(m -> m
                        .query(keyword)
                        .fields("username", "nickname", "bio")
                    )
                )
                .from(from)
                .size(pageSize)
            );
            
            // 执行搜索
            SearchResponse<SearchResultDTO> searchResponse = elasticsearchClient.search(searchRequest, SearchResultDTO.class);
            
            // 处理结果
            List<SearchResultDTO> results = new ArrayList<>();
            for (Hit<SearchResultDTO> hit : searchResponse.hits().hits()) {
                SearchResultDTO dto = hit.source();
                if (dto != null) {
                    dto.setId(hit.id() != null ? Long.valueOf(hit.id()) : null);
                    results.add(dto);
                }
            }
            
            return results;
        } catch (IOException e) {
            log.error("Elasticsearch 搜索用户失败: keyword={}", keyword, e);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Elasticsearch 搜索用户失败: keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }
}