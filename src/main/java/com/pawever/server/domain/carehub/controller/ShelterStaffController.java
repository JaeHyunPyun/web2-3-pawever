package com.pawever.server.domain.carehub.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.carehub.dto.request.SearchShelterRequestDTO;
import com.pawever.server.domain.carehub.dto.response.CareHubResponseDTO;
import com.pawever.server.domain.carehub.dto.response.ShelterPetsResponseDTO;
import com.pawever.server.domain.carehub.enums.Species;
import com.pawever.server.domain.carehub.service.CareHubService;
import com.pawever.server.domain.carehub.service.ShelterService;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.service.AccessTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/users/staff")
@Tag(name = "보호소 스태프 API")
public class ShelterStaffController {
    private final CareHubService careHubService;
    private final ShelterService shelterService;
    private final AccessTokenService accessTokenService;
    private final JwtUtil jwtUtil;

    //STAFF 페이지 - 보호중인 친구들
    @GetMapping("/pets")
    @Operation(summary = "보호소 스태프가 관리하는 동물 조회")
    public ResponseEntity<ApiResponse> getShelterPets(
            HttpServletRequest httpServletRequest
    ) {
        // 토큰에서 userId 추출
        String accessToken = accessTokenService.getRequestAccessToken(httpServletRequest);
        Long userId = jwtUtil.getUserId(accessToken);

        // 유저의 보호소 정보 조회
        Long providerShelterId = shelterService.getShelterId(userId);

        // 유기동물 조회
        List<ShelterPetsResponseDTO> abandonedPets = careHubService.getShelterPets(providerShelterId);

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, abandonedPets));
    }

}