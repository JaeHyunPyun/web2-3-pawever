package com.pawever.server.domain.carehub.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.carehub.service.ShelterService;
import com.pawever.server.domain.user.dto.response.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shelters")
public class ShelterController {

    private final ShelterService shelterService;

    @PostMapping("{shelter_id}/staff")
    public ResponseEntity<ApiResponse> registerShelterStaff(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("shelter_id")Long shelterId){
        shelterService.registerShelterStaff(customUserDetails.getUsername(),shelterId);
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS));
    }
}
