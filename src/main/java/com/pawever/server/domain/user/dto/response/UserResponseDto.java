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
public class UserResponseDto {
    private long userId;                        //유저 id
    private String socialLoginUuid;         //소셜로그인 uuid
    private String name;                    //소셜로그인 닉네임
    private Role role;                      //유저 권한
    private Boolean isDeleted;              //삭제 여부
    private String email;                   // 유저 email
}
