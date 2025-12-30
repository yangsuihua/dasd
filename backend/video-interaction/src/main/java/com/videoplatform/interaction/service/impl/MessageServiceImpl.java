package com.videoplatform.interaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.videoplatform.common.entity.Message;
import com.videoplatform.interaction.dto.MessageDTO;
import com.videoplatform.interaction.mapper.MessageMapper;
import com.videoplatform.interaction.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MessageMapper messageMapper;

    @Override
    public List<MessageDTO> listMessages(Long userId, Integer page, Integer size, Integer isRead) {
        if (userId == null) {
            return Collections.emptyList();
        }
        int current = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : size;
        Page<Message> pager = new Page<>(current, pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .eq(Message::getToUserId, userId)
                .orderByDesc(Message::getCreatedAt);
        if (isRead != null) {
            wrapper.eq(Message::getIsRead, isRead);
        }
        messageMapper.selectPage(pager, wrapper);
        return toDTOs(pager.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long userId, Long messageId) {
        if (userId == null || messageId == null) {
            return;
        }
        LambdaUpdateWrapper<Message> update = new LambdaUpdateWrapper<Message>()
                .eq(Message::getId, messageId)
                .eq(Message::getToUserId, userId)
                .set(Message::getIsRead, 1);
        messageMapper.update(null, update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendMessage(MessageDTO dto) {
        if (dto == null || dto.getToUserId() == null || dto.getType() == null) {
            return null;
        }
        Message message = new Message();
        message.setFromUserId(dto.getFromUserId());
        message.setToUserId(dto.getToUserId());
        message.setType(dto.getType());
        message.setContent(dto.getContent());
        message.setRelatedId(dto.getRelatedId());
        message.setIsRead(0);
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);
        return message.getId();
    }

    private List<MessageDTO> toDTOs(List<Message> messages) {
        List<MessageDTO> list = new ArrayList<MessageDTO>();
        for (Message message : messages) {
            MessageDTO dto = new MessageDTO();
            dto.setId(message.getId());
            dto.setFromUserId(message.getFromUserId());
            dto.setToUserId(message.getToUserId());
            dto.setType(message.getType());
            dto.setContent(message.getContent());
            dto.setRelatedId(message.getRelatedId());
            dto.setIsRead(message.getIsRead());
            dto.setCreatedAt(message.getCreatedAt() == null ? null : FORMATTER.format(message.getCreatedAt()));
            list.add(dto);
        }
        return list;
    }
}
