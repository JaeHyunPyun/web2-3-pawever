package com.pawever.server.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserProfileResponseDto {
    private String name;
    private String email;
    private String profileImageUrl;
}
