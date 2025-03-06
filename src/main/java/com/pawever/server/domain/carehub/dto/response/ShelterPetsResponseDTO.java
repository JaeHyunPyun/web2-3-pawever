package com.pawever.server.domain.carehub.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShelterPetsResponseDTO {
    private Long id;                     // 동물 ID
    private Long providerShelterId;      // 보호소 ID
    private String providerShelterName;  // 보호소 이름
    private String imageUrl;             // 동물 이미지 URL
    private String name;                 // 동물 이름
    private String species;              // 축종 (DOG, CAT)
    private String breed;                // 품종
    private String neuteredStatus;       // 중성화 상태
    private String sex;                  // 성별
    private String age;                  // 연령
    private String foundLocation;        // 발견 장소
    private String weight;               // 체중
    private String color;                // 색상
    private String characteristics;      // 특징
    private String noticeNumber;         // 공고번호
    private String cityCode;             // 시도 코드
    private String districtCode;         // 시군구 코드
}
