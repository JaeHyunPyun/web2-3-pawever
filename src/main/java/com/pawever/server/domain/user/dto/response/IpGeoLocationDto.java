package com.pawever.server.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IpGeoLocationDto {
    private String countryName;
    private String subdivisionName;
    private String cityName;
}
