package com.pawever.server.domain.recommendation.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.recommendation.dto.nearby.NearbyRecommendedAnimalResponse;
import com.pawever.server.domain.recommendation.dto.nearby.NearbyRecommendedAnimalsRequest;
import com.pawever.server.domain.recommendation.dto.recommendation.RecommendationRequest;
import com.pawever.server.domain.recommendation.dto.recommendation.RecommendationResponse;
import com.pawever.server.domain.recommendation.service.CatRecommendationService;
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

    private final DogRecommendationService dogRecommendationService;
    private final CatRecommendationService catRecommendationService;
    private final NearbyRecommendedAnimalsService nearbyRecommendedAnimalsService;

    @PostMapping("/dog")
    public ResponseEntity<ApiResponse> recommendDog(@RequestBody RecommendationRequest request) {
        List<RecommendationResponse> recommendations = dogRecommendationService.recommendDogs(request.getResponses());

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, recommendations));
    }

    @PostMapping("/cat")
    public ResponseEntity<ApiResponse> recommendCat(@RequestBody RecommendationRequest request) {
        List<RecommendationResponse> recommendations = catRecommendationService.recommendCats(request.getResponses());

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