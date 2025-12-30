package com.videoplatform.admin.controller;

import com.videoplatform.admin.service.AuditService;
import com.videoplatform.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    /**
     * 审核视频
     */
    @PostMapping("/video/{videoId}")
    public Result<String> auditVideo(@PathVariable Long videoId, 
                                   @RequestParam String action,
                                   @RequestParam(required = false) String reason) {
        log.info("审核视频: videoId={}, action={}", videoId, action);
        // TODO: 实现视频审核逻辑
        auditService.recordAuditLog(videoId, "video", action, reason, 1);
        return Result.success("审核成功");
    }

    /**
     * 审核用户
     */
    @PostMapping("/user/{userId}")
    public Result<String> auditUser(@PathVariable Long userId,
                                  @RequestParam String action,
                                  @RequestParam(required = false) String reason) {
        log.info("审核用户: userId={}, action={}", userId, action);
        // TODO: 实现用户审核逻辑
        auditService.recordAuditLog(userId, "user", action, reason, 1);
        return Result.success("审核成功");
    }
}