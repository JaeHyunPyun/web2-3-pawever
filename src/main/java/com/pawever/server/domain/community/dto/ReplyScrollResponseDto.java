package com.pawever.server.domain.community.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReplyScrollResponseDto {
    private List<ReplyResponseDto> replies;
    private Long lastReplyId; // 무한 스크롤을 위한 마지막 댓글 ID
    private boolean hasMore;  // 더 불러올 댓글이 있는지 여부

    public ReplyScrollResponseDto(List<ReplyResponseDto> replies, Long lastReplyId, boolean hasMore) {
        this.replies = replies;
        this.lastReplyId = lastReplyId;
        this.hasMore = hasMore;
    }

    // Getters
    public List<ReplyResponseDto> getReplies() {
        return replies;
    }

    public Long getLastReplyId() {
        return lastReplyId;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}