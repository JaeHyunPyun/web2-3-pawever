package com.pawever.server.domain.carehub.dto.request;

import jakarta.validation.constraints.NotBlank;

public class SearchShelterRequestDTO {
    public record SearchShelterRequest(
            @NotBlank String cityName,      // 시도명
            @NotBlank String districtName   // 시군구
    ) {}
}
