package com.pawever.server.domain.user.dto.request;

import com.pawever.server.domain.user.constant.UserConstants;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {
    private String socialLoginUuid;         //소셜로그인 uuid
    private String name;                    //소셜로그인 닉네임
    //프로필 이미지
    @Builder.Default
    private String profileImageUrl= UserConstants.DEFAULT_PROFILE_IMAGE_URL;
    private String email;                   //소셜로그인 이메일
    private String socialLoginProvider;     //카카오 or 구글
    private BigDecimal latitude;            //사용자 위치 - 위도
    private BigDecimal longitude;           //사용자 위치 - 경도
}
