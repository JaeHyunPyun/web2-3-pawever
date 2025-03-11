package com.pawever.server.domain.LikedPet.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.LikedPet.dto.LikedPetResponse;
import com.pawever.server.domain.LikedPet.service.LikedPetService;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.service.AccessTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "사용자가 좋아요한 동물 관리 API")
public class LikedPetController {

    private final LikedPetService likedPetService;
    private final AccessTokenService accessTokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/animals/{animalId}/like-toggle")
    @Operation(summary = "좋아요 토글 API")
    public ResponseEntity<ApiResponse> toggleLike(
            @PathVariable Long animalId,
            HttpServletRequest httpServletRequest) {

        Long userId = getUserIdFromToken(httpServletRequest);
        boolean liked = likedPetService.toggleLike(userId, animalId);

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, Map.of("liked", liked)));
    }


    @GetMapping("/animals/{animalId}/is-liked")
    @Operation(summary = "특정 동물 좋아요 여부 조회")
    public ResponseEntity<ApiResponse> isLiked(
            @PathVariable Long animalId,
            HttpServletRequest httpServletRequest) {

        Long userId = getUserIdFromToken(httpServletRequest);
        boolean liked = likedPetService.isLiked(userId, animalId);

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, Map.of("liked", liked)));
    }


    @GetMapping("/users/liked-animals")
    @Operation(summary = "좋아요한 동물 목록 조회")
    public ResponseEntity<ApiResponse> getLikedAnimals(HttpServletRequest httpServletRequest) {
        Long userId = getUserIdFromToken(httpServletRequest);
        List<LikedPetResponse> likedAnimals = likedPetService.getLikedAnimals(userId);

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, likedAnimals));
    }

    @DeleteMapping("/animals/{animalId}/like")
    @Operation(summary = "좋아요 삭제 API")
    public ResponseEntity<ApiResponse> deleteLike(
            @PathVariable Long animalId,
            HttpServletRequest httpServletRequest) {

        Long userId = getUserIdFromToken(httpServletRequest);
        likedPetService.deleteLikedPet(userId, animalId);

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS));
    }


    private Long getUserIdFromToken(HttpServletRequest request) {
        String accessToken = accessTokenService.getRequestAccessToken(request);
        return jwtUtil.getUserId(accessToken);
    }
}
