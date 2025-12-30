package com.videoplatform.interaction.service;

import com.videoplatform.interaction.dto.MessageDTO;

import java.util.List;

public interface MessageService {
    List<MessageDTO> listMessages(Long userId, Integer page, Integer size, Integer isRead);

    void markRead(Long userId, Long messageId);

    Long sendMessage(MessageDTO dto);
}
