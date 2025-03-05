package com.pawever.server.domain.community.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReplyListResponseDto {
    private List<ReplyResponseDto> replies;

    public ReplyListResponseDto(List<ReplyResponseDto> replies) {
        this.replies = replies;
    }

}