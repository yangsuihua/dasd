package com.videoplatform.admin.controller;

import com.videoplatform.admin.dto.AdminStatsDTO;
import com.videoplatform.admin.service.AdminService;
import com.videoplatform.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 管理后台首页
     */
    @GetMapping("/dashboard")
    public Result<String> dashboard() {
        log.info("访问管理后台首页");
        return Result.success("欢迎访问管理后台");
    }

    /**
     * 获取系统统计信息
     */
    @GetMapping("/stats")
    public Result<AdminStatsDTO> getStats() {
        log.info("获取系统统计信息");
        // TODO: 实现具体的统计逻辑
        return Result.success(new AdminStatsDTO());
    }
}