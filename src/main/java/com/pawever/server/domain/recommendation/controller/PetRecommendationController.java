package com.pawever.server.domain.recommendation.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.recommendation.dto.nearby.NearbyRecommendedAnimalResponse;
import com.pawever.server.domain.recommendation.dto.nearby.NearbyRecommendedAnimalsRequest;
import com.pawever.server.domain.recommendation.dto.recommendation.RecommendationRequest;
import com.pawever.server.domain.recommendation.dto.recommendation.RecommendationResponse;
import com.pawever.server.domain.recommendation.service.DogRecommendationService;
import com.pawever.server.domain.recommendation.service.NearbyRecommendedAnimalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend-animals")
@RequiredArgsConstructor
public class PetRecommendationController {

    private final DogRecommendationService recommendationService;
    private final NearbyRecommendedAnimalsService nearbyRecommendedAnimalsService;

    @PostMapping
    public ResponseEntity<ApiResponse> recommendAnimals(@RequestBody RecommendationRequest request) {
        List<RecommendationResponse> recommendations = recommendationService.recommendPets(request.getResponses());

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, recommendations));
    }

    @PostMapping("/nearby")
    public ResponseEntity<ApiResponse> getNearbyRecommendedAnimals(
            @RequestBody NearbyRecommendedAnimalsRequest request) {

        List<NearbyRecommendedAnimalResponse> animals =
                nearbyRecommendedAnimalsService.findNearbyRecommendedAnimals(request);

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, animals));
    }


}