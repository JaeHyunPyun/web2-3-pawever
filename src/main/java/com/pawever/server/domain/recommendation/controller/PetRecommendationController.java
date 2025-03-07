package com.pawever.server.domain.recommendation.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.recommendation.dto.nearby.NearbyRecommendedAnimalResponse;
import com.pawever.server.domain.recommendation.dto.nearby.NearbyRecommendedAnimalsRequest;
import com.pawever.server.domain.recommendation.dto.recommendation.QuestionResponse;
import com.pawever.server.domain.recommendation.dto.recommendation.RecommendationRequest;
import com.pawever.server.domain.recommendation.dto.recommendation.RecommendationResponse;
import com.pawever.server.domain.recommendation.service.CatRecommendationService;
import com.pawever.server.domain.recommendation.service.DogRecommendationService;
import com.pawever.server.domain.recommendation.service.NearbyRecommendedAnimalsService;
import com.pawever.server.domain.recommendation.service.QuestionService;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.service.AccessTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend-animals")
@RequiredArgsConstructor
@Tag(name = "동물 매칭 & 추천 API")
public class PetRecommendationController {

    private final DogRecommendationService dogRecommendationService;
    private final CatRecommendationService catRecommendationService;
    private final NearbyRecommendedAnimalsService nearbyRecommendedAnimalsService;
    private final AccessTokenService accessTokenService;
    private final JwtUtil jwtUtil;
    private final QuestionService questionService;

    @GetMapping("/dogs/questions/{questionId}")
    @Operation(summary = "개 매칭 질문 API")
    public ResponseEntity<ApiResponse> getDogQuestion(@PathVariable Long questionId) {
        QuestionResponse questionResponse =  questionService.getDogQuestion(questionId);
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, questionResponse));
    }

    @GetMapping("/cats/questions/{questionId}")
    @Operation(summary = "고양이 매칭 질문 API")
    public ResponseEntity<ApiResponse> getCatQuestion(@PathVariable Long questionId) {
        QuestionResponse questionResponse =  questionService.getCatQuestion(questionId);
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, questionResponse));
    }

    @PostMapping("/dog")
    @Operation(summary = "개 매칭 API")
    public ResponseEntity<ApiResponse> recommendDog( @RequestBody RecommendationRequest request) {
        List<RecommendationResponse> recommendations = dogRecommendationService.recommendDogs(request.getResponses());

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, recommendations));
    }

    @PostMapping("/cat")
    @Operation(summary = "고양이 매칭 API")
    public ResponseEntity<ApiResponse> recommendCat( @RequestBody RecommendationRequest request) {
        List<RecommendationResponse> recommendations = catRecommendationService.recommendCats(request.getResponses());

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, recommendations));
    }

    @PostMapping("/nearby")
    @Operation(summary = "매칭된 품종 중에서 가까운 4마리 추천 API")
    public ResponseEntity<ApiResponse> getNearbyRecommendedAnimals(
             @RequestBody NearbyRecommendedAnimalsRequest request,
            HttpServletRequest httpServletRequest) {

        String accessToken = accessTokenService.getRequestAccessToken(httpServletRequest);
        Long userId =  jwtUtil.getUserId(accessToken);

        List<NearbyRecommendedAnimalResponse> animals =
                nearbyRecommendedAnimalsService.findNearbyRecommendedAnimals(request, userId);

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, animals));
    }


}