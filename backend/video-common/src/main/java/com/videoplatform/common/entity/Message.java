package com.videoplatform.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    /**
     * 消息类型: 1-系统, 2-点赞, 3-评论, 4-关注, 5-私信
     */
    private Integer type;
    private String content;
    /**
     * 关联ID (视频ID/评论ID等)
     */
    private Long relatedId;
    /**
     * 是否已读: 0-未读, 1-已读
     */
    private Integer isRead;
    private LocalDateTime createdAt;
}
