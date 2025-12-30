package com.videoplatform.interaction.controller;

import com.videoplatform.common.result.Result;
import com.videoplatform.interaction.dto.MessageDTO;
import com.videoplatform.interaction.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public Result<List<MessageDTO>> list(@RequestHeader("X-User-Id") Long userId,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestParam(required = false) Integer isRead) {
        return Result.success(messageService.listMessages(userId, page, size, isRead));
    }

    @PostMapping
    public Result<Long> send(@RequestBody MessageDTO dto) {
        Long id = messageService.sendMessage(dto);
        return Result.success(id);
    }

    @PostMapping("/{messageId}/read")
    public Result<Void> markRead(@RequestHeader("X-User-Id") Long userId, @PathVariable Long messageId) {
        messageService.markRead(userId, messageId);
        return Result.success();
    }
}
