package com.pawever.server.domain.carehub.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AbandonedPetDetailResponse {

    //유기동물
    private String name;
    private Long id;
    private String neuteredStatus;
    private String weight;
    private String color;
    private String characteristics;
    private String imageUrl;

    //보호소
    private String shelterName;
    private String shelterPhoneNumber;
    private String shelterRoadAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
