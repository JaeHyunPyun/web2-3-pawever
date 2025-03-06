package com.pawever.server.domain.carehub.controller;
import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.carehub.dto.response.AbandonedPetDetailResponse;
import com.pawever.server.domain.carehub.service.AbandonedPetDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
@Tag(name = "유기동물 상세 조회 API")
public class AbandonedPetController {
    private final AbandonedPetDetailService abandonedPetDetailService;

    @GetMapping("/{petId}")
    @Operation(summary = "특정 유기동물 상세 정보 조회")
    public ResponseEntity<ApiResponse> getAbandonedPetDetail(@PathVariable Long petId) {
        AbandonedPetDetailResponse response = abandonedPetDetailService.getAbandonedPetDetail(petId);
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }
}