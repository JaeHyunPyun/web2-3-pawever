package com.pawever.server.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class StaffProfileResponseDto {
    private String userName;
    private String userEmail;
    private String userProfileImageUrl;
    private String shelterName;
    private String shelterCenterPhoneNumber;
    private String shelterManagerPhoneNumber;
}
