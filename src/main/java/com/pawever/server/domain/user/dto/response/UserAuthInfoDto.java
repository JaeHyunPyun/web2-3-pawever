package com.pawever.server.domain.user.dto.response;

import com.pawever.server.domain.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthInfoDto {
    private String socialLoginUuid;
    private Role role;
}
