package com.pawever.server.domain.LikedPet.dto;

import com.pawever.server.domain.carehub.enums.Sex;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikedPetResponse {
    private Long id;
    private String name;
    private String age;
    private Sex sex;
    private String imageUrl;

    private String shelterName;
    private Double distanceToShelter;
}
