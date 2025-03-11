package com.pawever.server.domain.post.dto.response;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDTO {
    public record PostResponse(
            Long id,
            String userUuid,
            String author,
            String profileImage,
            String thumbnailImage,
            String title,
            String content,
            List<String> images,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
