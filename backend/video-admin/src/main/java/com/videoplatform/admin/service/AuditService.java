package com.videoplatform.admin.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.videoplatform.admin.entity.AuditLog;
import com.videoplatform.admin.mapper.AuditLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService extends ServiceImpl<AuditLogMapper, AuditLog> {
    
    private final AuditLogMapper auditLogMapper;
    
    /**
     * 记录审核日志
     */
    public void recordAuditLog(Long targetId, String targetType, String action, String reason, Integer status) {
        AuditLog auditLog = new AuditLog();
        auditLog.setTargetId(targetId);
        auditLog.setTargetType(targetType);
        auditLog.setAction(action);
        auditLog.setReason(reason);
        auditLog.setStatus(status);
        auditLogMapper.insert(auditLog);
        log.info("记录审核日志: targetId={}, targetType={}, action={}", targetId, targetType, action);
    }
}