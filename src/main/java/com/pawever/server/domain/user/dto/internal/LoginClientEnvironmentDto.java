package com.pawever.server.domain.user.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginClientEnvironmentDto {
    String clientIp;
    String clientOs;
    String clientBrowser;
}
