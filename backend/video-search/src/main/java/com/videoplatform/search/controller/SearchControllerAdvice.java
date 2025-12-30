package com.videoplatform.search.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class SearchControllerAdvice {
    
    /**
     * 在每个搜索请求之前记录日志
     */
    @ModelAttribute
    public void logSearchRequest(HttpServletRequest request) {
        if (request.getRequestURI().contains("/search")) {
            log.info("收到搜索请求: method={}, uri={}, queryString={}", 
                    request.getMethod(), 
                    request.getRequestURI(), 
                    request.getQueryString());
        }
    }
}