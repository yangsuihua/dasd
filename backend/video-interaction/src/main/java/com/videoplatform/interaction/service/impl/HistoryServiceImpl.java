package com.videoplatform.interaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.videoplatform.common.entity.UserHistory;
import com.videoplatform.interaction.mapper.UserHistoryMapper;
import com.videoplatform.interaction.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final UserHistoryMapper userHistoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordHistory(Long userId, Long videoId, Integer watchDuration, Double watchProgress) {
        if (userId == null || videoId == null) {
            return;
        }
        UserHistory exist = userHistoryMapper.selectOne(new LambdaQueryWrapper<UserHistory>()
                .eq(UserHistory::getUserId, userId)
                .eq(UserHistory::getVideoId, videoId));
        LocalDateTime now = LocalDateTime.now();
        if (exist == null) {
            UserHistory history = new UserHistory();
            history.setUserId(userId);
            history.setVideoId(videoId);
            history.setWatchDuration(watchDuration);
            history.setWatchProgress(watchProgress);
            history.setCreatedAt(now);
            history.setUpdatedAt(now);
            userHistoryMapper.insert(history);
        } else {
            LambdaUpdateWrapper<UserHistory> update = new LambdaUpdateWrapper<UserHistory>()
                    .eq(UserHistory::getId, exist.getId())
                    .set(UserHistory::getWatchDuration, watchDuration)
                    .set(UserHistory::getWatchProgress, watchProgress)
                    .set(UserHistory::getUpdatedAt, now);
            userHistoryMapper.update(null, update);
        }
    }

    @Override
    public List<UserHistory> listHistory(Long userId, Integer page, Integer size) {
        if (userId == null) {
            return Collections.emptyList();
        }
        int current = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : size;
        Page<UserHistory> pager = new Page<>(current, pageSize);
        LambdaQueryWrapper<UserHistory> wrapper = new LambdaQueryWrapper<UserHistory>()
                .eq(UserHistory::getUserId, userId)
                .orderByDesc(UserHistory::getUpdatedAt);
        userHistoryMapper.selectPage(pager, wrapper);
        return pager.getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHistory(Long userId, Long historyId) {
        if (userId == null || historyId == null) {
            return;
        }
        userHistoryMapper.delete(new LambdaQueryWrapper<UserHistory>()
                .eq(UserHistory::getId, historyId)
                .eq(UserHistory::getUserId, userId));
    }
}
