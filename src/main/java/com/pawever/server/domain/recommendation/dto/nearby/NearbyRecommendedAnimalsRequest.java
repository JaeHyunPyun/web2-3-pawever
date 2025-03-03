package com.pawever.server.domain.recommendation.dto.nearby;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyRecommendedAnimalsRequest {
    private String recommendedBreed; // 추천된 견종 이름
    private double userLatitude;     // 사용자의 위도
    private double userLongitude;    // 사용자의     경도
}