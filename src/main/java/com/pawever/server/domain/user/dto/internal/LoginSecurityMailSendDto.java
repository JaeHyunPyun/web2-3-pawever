package com.pawever.server.domain.user.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginSecurityMailSendDto {
    private String emailAddr;           // 수신자 이메일
    @Builder.Default
    private String subject = "PAWEVER Notification : 새로운 환경에서 로그인되었습니다.";  // 이메일 제목
    private String userName;              // 수신자 User 아이디
}
