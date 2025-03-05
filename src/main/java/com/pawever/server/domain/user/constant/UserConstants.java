package com.pawever.server.domain.user.constant;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserConstants {

    @Value("${paw.user.default-image-url}") // 환경변수 주입
    private String defaultProfileImageUrl;

    public static String DEFAULT_PROFILE_IMAGE_URL;

    @PostConstruct
    private void init() {
        DEFAULT_PROFILE_IMAGE_URL = defaultProfileImageUrl;
    }
}
