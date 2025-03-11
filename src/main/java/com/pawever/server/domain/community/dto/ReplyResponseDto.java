package com.pawever.server.domain.community.dto;

import com.pawever.server.domain.community.entity.Reply;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReplyResponseDto {
    private Long replyId;
    private Long userId;
    private String userUuid;
    private String username;
    private String profileImageUrl;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity -> DTO 변환
    public ReplyResponseDto(Reply reply) {
        this.replyId = reply.getReplyId();
        this.userId = reply.getUser().getUserId();
        this.userUuid = reply.getUser().getSocialLoginUuid();
        this.username = reply.getUser().getName();
        this.profileImageUrl = reply.getUser().getProfileImageUrl();
        this.postId = reply.getPost().getId();
        this.content = reply.getContent();
        this.createdAt = reply.getCreatedAt();
        this.updatedAt = reply.getUpdatedAt();
    }


}
