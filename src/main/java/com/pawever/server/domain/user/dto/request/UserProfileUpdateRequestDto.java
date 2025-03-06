package com.pawever.server.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserProfileUpdateRequestDto {
    private final String name;
    private final String introduction;
}
