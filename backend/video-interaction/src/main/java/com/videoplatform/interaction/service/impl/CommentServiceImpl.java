package com.videoplatform.interaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.videoplatform.common.entity.Comment;
import com.videoplatform.common.entity.User;
import com.videoplatform.common.entity.Video;
import com.videoplatform.interaction.dto.CommentDTO;
import com.videoplatform.interaction.mapper.CommentMapper;
import com.videoplatform.interaction.mapper.UserMapper;
import com.videoplatform.interaction.mapper.VideoMapper;
import com.videoplatform.interaction.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 评论服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CommentMapper commentMapper;
    private final VideoMapper videoMapper;
    private final UserMapper userMapper;
    
    @Override
    public List<CommentDTO> getComments(Long videoId, Integer page, Integer size) {
        if (videoId == null) {
            return Collections.emptyList();
        }
        int current = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : size;

        Page<Comment> pager = new Page<>(current, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getVideoId, videoId)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreatedAt);
        commentMapper.selectPage(pager, wrapper);

        List<Comment> comments = pager.getRecords();
        return toDTOs(comments);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentDTO postComment(CommentDTO dto) {
        if (dto == null || dto.getUserId() == null || dto.getVideoId() == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment();
        comment.setVideoId(dto.getVideoId());
        comment.setUserId(dto.getUserId());
        comment.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        comment.setReplyUserId(dto.getReplyUserId());
        comment.setContent(dto.getContent());
        comment.setLikeCount(0);
        comment.setStatus(1);
        comment.setCreatedAt(now);
        commentMapper.insert(comment);

        LambdaUpdateWrapper<Video> update = new LambdaUpdateWrapper<Video>()
                .eq(Video::getId, dto.getVideoId())
                .setSql("comment_count = comment_count + 1");
        videoMapper.update(null, update);

        dto.setId(comment.getId());
        dto.setLikeCount(comment.getLikeCount());
        dto.setCreatedAt(DATE_TIME_FORMATTER.format(now));
        Map<Long, User> users = loadUsers(comment);
        User author = users.get(comment.getUserId());
        if (author != null) {
            dto.setUsername(author.getUsername());
            dto.setAvatar(author.getAvatar());
        }
        User replyUser = users.get(comment.getReplyUserId());
        if (replyUser != null) {
            dto.setReplyUsername(replyUser.getUsername());
        }
        dto.setIsLiked(Boolean.FALSE);
        return dto;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long userId, Long commentId) {
        if (userId == null || commentId == null) {
            return;
        }
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            return;
        }
        if (!userId.equals(comment.getUserId())) {
            throw new IllegalArgumentException("无权限删除该评论");
        }
        LambdaUpdateWrapper<Comment> updateComment = new LambdaUpdateWrapper<Comment>()
                .eq(Comment::getId, commentId)
                .set(Comment::getStatus, 0);
        commentMapper.update(null, updateComment);

        LambdaUpdateWrapper<Video> updateVideo = new LambdaUpdateWrapper<Video>()
                .eq(Video::getId, comment.getVideoId())
                .setSql("comment_count = CASE WHEN comment_count > 0 THEN comment_count - 1 ELSE 0 END");
        videoMapper.update(null, updateVideo);
    }

    private List<CommentDTO> toDTOs(List<Comment> comments) {
        if (CollectionUtils.isEmpty(comments)) {
            return Collections.emptyList();
        }
        Set<Long> userIds = new HashSet<Long>();
        for (Comment comment : comments) {
            if (comment.getUserId() != null) {
                userIds.add(comment.getUserId());
            }
            if (comment.getReplyUserId() != null) {
                userIds.add(comment.getReplyUserId());
            }
        }
        Map<Long, User> userMap = loadUsers(userIds);

        List<CommentDTO> list = new ArrayList<CommentDTO>(comments.size());
        for (Comment comment : comments) {
            CommentDTO dto = new CommentDTO();
            dto.setId(comment.getId());
            dto.setVideoId(comment.getVideoId());
            dto.setUserId(comment.getUserId());
            dto.setParentId(comment.getParentId());
            dto.setReplyUserId(comment.getReplyUserId());
            dto.setContent(comment.getContent());
            dto.setLikeCount(comment.getLikeCount());
            dto.setCreatedAt(comment.getCreatedAt() == null ? null : DATE_TIME_FORMATTER.format(comment.getCreatedAt()));
            User author = userMap.get(comment.getUserId());
            if (author != null) {
                dto.setUsername(author.getUsername());
                dto.setAvatar(author.getAvatar());
            }
            User replyUser = userMap.get(comment.getReplyUserId());
            if (replyUser != null) {
                dto.setReplyUsername(replyUser.getUsername());
            }
            dto.setIsLiked(Boolean.FALSE);
            list.add(dto);
        }
        return list;
    }

    private Map<Long, User> loadUsers(Comment comment) {
        Set<Long> ids = new HashSet<Long>();
        if (comment.getUserId() != null) {
            ids.add(comment.getUserId());
        }
        if (comment.getReplyUserId() != null) {
            ids.add(comment.getReplyUserId());
        }
        return loadUsers(ids);
    }

    private Map<Long, User> loadUsers(Set<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<User> users = userMapper.selectBatchIds(new ArrayList<Long>(userIds));
        Map<Long, User> map = new HashMap<Long, User>();
        for (User user : users) {
            map.put(user.getId(), user);
        }
        return map;
    }
}
