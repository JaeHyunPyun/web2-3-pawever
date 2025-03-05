package com.pawever.server.domain.carehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CareHubResponseDTO {
    private Long id;                     // 동물 ID
    private Long providerShelterId;      // 보호소 ID
    private String providerShelterName;  // 보호소 이름
    private String imageUrl;             // 동물 이미지 URL
    private String name;                 // 동물 이름
    private String neuteredStatus;       // 중성화 상태
    private String[] characteristics;    // 성별, 몸무게, 특징
}

