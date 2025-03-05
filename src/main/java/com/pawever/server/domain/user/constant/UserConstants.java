package com.pawever.server.domain.user.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserConstants {
    // user디폴트 이미지 주소
    @Value("${paw.user.default-image-url}")
    public static String DEFAULT_PROFILE_IMAGE_URL;

    // static 변수에 값을 주입하기 위한 setter
    @Value("${paw.user.default-image-url}")
    public void setDefaultProfileImageUrl(String url) {
        DEFAULT_PROFILE_IMAGE_URL = url;
    }
}
