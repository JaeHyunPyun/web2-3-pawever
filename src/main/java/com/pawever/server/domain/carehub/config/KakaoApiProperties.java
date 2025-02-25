package com.pawever.server.domain.carehub.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kakao.api")
public class KakaoApiProperties {
    private String key;
}