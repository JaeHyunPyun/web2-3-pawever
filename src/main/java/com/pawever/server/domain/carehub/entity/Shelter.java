package com.pawever.server.domain.carehub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "shelter")
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;  // 보호소ID

    @Column(name = "user_id")
    private Long userId;  // 사용자ID (NULL일 경우 보호소 관계자 아님)

    @Column(name = "provider_shelter_id", nullable = false)
    private Long providerShelterId;  // API에서 제공하는 보호소 고유 ID

    @Column(name = "name", nullable = false, length = 255)
    private String name;  // 보호소 이름

    @Column(name = "center_phone_number", nullable = true, length = 255)
    private String centerPhoneNumber;  // 보호소 전화번호

    @Column(name = "manager_phone_number", nullable = true, length = 255)
    private String managerPhoneNumber;  // 보호소 담당자 전화번호

    @Column(name = "operation_start_time", nullable = false)
    @Builder.Default
    private LocalTime operationStartTime = LocalTime.of(9, 0);  // 보호소 운영 시작 시간 (기본값 09:00:00)

    @Column(name = "operation_end_time", nullable = false)
    @Builder.Default
    private LocalTime operationEndTime = LocalTime.of(18, 0);  // 보호소 운영 끝 시간 (기본값 18:00:00)

    @Column(name = "city_code", nullable = false)
    private String cityCode;

    @Column(name = "district_code", nullable = false)
    private String districtCode;

    @Column(name = "sido", nullable = true, length = 50)
    private String sido;  // 시/도

    @Column(name = "sigungu", nullable = true, length = 50)
    private String sigungu;  // 시/군/구

    @Column(name = "eupmyeondong", nullable = true, length = 50)
    private String eupmyeondong;  // 읍/면/동

    @Column(name = "road_address", nullable = true, length = 255)
    private String roadAddress;  // 상세주소

    @Column(name = "latitude", nullable = true, precision = 10, scale = 7)
    private BigDecimal latitude;  // 위도

    @Column(name = "longitude", nullable = true, precision = 10, scale = 7)
    private BigDecimal longitude;  // 경도
}
