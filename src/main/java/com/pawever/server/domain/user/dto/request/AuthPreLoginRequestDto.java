package com.pawever.server.domain.user.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthPreLoginRequestDto {
    private String codeChallenge;
    // codeChallengeMethod가 전달되지 않는 경우 plain을 default로 설정하도록 세팅
    @Builder.Default
    private String codeChallengeMethod="plain";
}
