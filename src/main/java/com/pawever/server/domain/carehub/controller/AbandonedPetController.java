package com.pawever.server.domain.carehub.controller;
import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.carehub.dto.response.AbandonedPetDetailResponse;
import com.pawever.server.domain.carehub.service.AbandonedPetDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AbandonedPetController {
    private final AbandonedPetDetailService abandonedPetDetailService;

    @GetMapping("/{petId}")
    public ResponseEntity<ApiResponse> getAbandonedPetDetail(@PathVariable Long petId) {
        AbandonedPetDetailResponse response = abandonedPetDetailService.getAbandonedPetDetail(petId);
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }
}