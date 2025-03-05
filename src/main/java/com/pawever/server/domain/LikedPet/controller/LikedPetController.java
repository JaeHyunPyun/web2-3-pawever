package com.pawever.server.domain.LikedPet.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.LikedPet.dto.LikedPetResponse;
import com.pawever.server.domain.LikedPet.service.LikedPetService;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.service.AccessTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikedPetController {

    private final LikedPetService likedPetService;
    private final AccessTokenService accessTokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/animals/{animalId}/like-toggle")
    public ResponseEntity<ApiResponse> toggleLike(
            @PathVariable Long animalId,
            HttpServletRequest httpServletRequest) {

        Long userId = getUserIdFromToken(httpServletRequest);
        boolean liked = likedPetService.toggleLike(userId, animalId);

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, Map.of("liked", liked)));
    }


    @GetMapping("/animals/{animalId}/is-liked")
    public ResponseEntity<ApiResponse> isLiked(
            @PathVariable Long animalId,
            HttpServletRequest httpServletRequest) {

        Long userId = getUserIdFromToken(httpServletRequest);
        boolean liked = likedPetService.isLiked(userId, animalId);

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, Map.of("liked", liked)));
    }


    @GetMapping("/users/liked-animals")
    public ResponseEntity<ApiResponse> getLikedAnimals(HttpServletRequest httpServletRequest) {
        Long userId = getUserIdFromToken(httpServletRequest);
        List<LikedPetResponse> likedAnimals = likedPetService.getLikedAnimals(userId);

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, likedAnimals));
    }

    private Long getUserIdFromToken(HttpServletRequest request) {
        String accessToken = accessTokenService.getRequestAccessToken(request);
        return jwtUtil.getUserId(accessToken);
    }
}
