package com.pawever.server.domain.carehub.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.carehub.dto.response.CareHubResponseDTO;
import com.pawever.server.domain.carehub.enums.Species;
import com.pawever.server.domain.carehub.service.CareHubService;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.service.AccessTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/animals")
public class CareHubController {
    private final CareHubService careHubService;
    private final AccessTokenService accessTokenService;
    private final JwtUtil jwtUtil;

    //유기동물 정보 페이지네이션해서 가져오기 (아직은 필터링 X) : 메인화면
    @GetMapping()
    public ResponseEntity<ApiResponse> getAbandonedPets(
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        Page<CareHubResponseDTO> abandonedPets = careHubService.getAbandonedPets(page, 5);

        Map<String, Object> response = new HashMap<>();
        response.put("content", abandonedPets.getContent());
        response.put("totalPages", abandonedPets.getTotalPages());

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }

    //유기동물 정보 페이지네이션해서 가져오기 (아직은 필터링 X) : 유기동물 조회 페이지
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> getFilteredAbandonedPets(
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        Page<CareHubResponseDTO> abandonedPets = careHubService.getAbandonedPets(page, 30);

        Map<String, Object> response = new HashMap<>();
        response.put("content", abandonedPets.getContent());
        response.put("totalPages", abandonedPets.getTotalPages());

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }


    //사용자에게 가까운 강아지 조회 - 반경 15KM로 임의 설정
    @GetMapping("/nearby/dogs")
    public ResponseEntity<ApiResponse> getNearbyDogs(
            @RequestParam(defaultValue = "0") int page,
            HttpServletRequest httpServletRequest
    ) {
        // 토큰에서 userId 추출
        String accessToken = accessTokenService.getRequestAccessToken(httpServletRequest);
        Long userId = jwtUtil.getUserId(accessToken);

        // 유기동물 조회
        Page<CareHubResponseDTO> abandonedPets = careHubService.getNearbyAbandonedPets(userId, 15, page, 5, Species.DOG);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("content", abandonedPets.getContent());
        response.put("totalPages", abandonedPets.getTotalPages());

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }

    //사용자에게 가까운 고양이 조회 - 반경 15KM로 임의 설정
    @GetMapping("/nearby/cats")
    public ResponseEntity<ApiResponse> getNearbyCats(
            @RequestParam(defaultValue = "0") int page,
            HttpServletRequest httpServletRequest
    ) {
        // 토큰에서 userId 추출
        String accessToken = accessTokenService.getRequestAccessToken(httpServletRequest);
        Long userId = jwtUtil.getUserId(accessToken);

        // 유기동물 조회
        Page<CareHubResponseDTO> abandonedPets = careHubService.getNearbyAbandonedPets(userId, 15, page, 5, Species.CAT);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("content", abandonedPets.getContent());
        response.put("totalPages", abandonedPets.getTotalPages());

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }

}