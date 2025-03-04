package com.pawever.server.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;

public class PostRequestDTO {
    public record CreatePostRequest(
            @NotBlank String title,      // 게시글 제목
            @NotBlank String content // 게시글 내용
    ) {}

    public record UpdatePostRequest(
            String title,
            String content
    ) {}
}
